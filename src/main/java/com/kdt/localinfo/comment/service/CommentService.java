package com.kdt.localinfo.comment.service;

import com.kdt.localinfo.aws.service.AwsS3Service;
import com.kdt.localinfo.comment.converter.CommentConverter;
import com.kdt.localinfo.comment.dto.CommentChangeRequest;
import com.kdt.localinfo.comment.dto.CommentResponse;
import com.kdt.localinfo.comment.dto.CommentSaveRequest;
import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.comment.repository.CommentRepository;
import com.kdt.localinfo.photo.CommentPhoto;
import com.kdt.localinfo.photo.CommentPhotoRepository;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.post.repository.PostRepository;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentConverter commentConverter;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentPhotoRepository commentPhotoRepository;
    private final AwsS3Service s3Uploader;

    public CommentService(CommentRepository commentRepository,
                          CommentConverter commentConverter,
                          UserRepository userRepository,
                          PostRepository postRepository,
                          CommentPhotoRepository commentPhotoRepository,
                          AwsS3Service s3Uploader) {
        this.commentRepository = commentRepository;
        this.commentConverter = commentConverter;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentPhotoRepository = commentPhotoRepository;
        this.s3Uploader = s3Uploader;
    }

    @Transactional
    public CommentResponse save(CommentSaveRequest commentSaveRequest, Long postId, List<MultipartFile> multipartFiles) throws NotFoundException, IOException {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시물에 대한 정보를 찾을 수 없습니다."));

        Long userId = commentSaveRequest.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("작성자 정보를 찾을 수 없습니다."));

        Comment comment = commentConverter.converterToComment(commentSaveRequest, user, post);

        Comment commentEntity = commentRepository.save(comment);

        List<String> fileUrls = fileUpload(multipartFiles);

        List<CommentPhoto> commentPhotos = fileUrls.stream()
                .map(url -> commentConverter.converterToCommentPhoto(comment, url))
                .collect(Collectors.toList());

        List<CommentPhoto> savedPhotos = commentPhotoRepository.saveAll(commentPhotos);

        List<String> urls = new ArrayList<>();
        savedPhotos.forEach(commentPhoto -> urls.add(commentPhoto.getUrl()));

        return commentConverter.converterToCommentResponse(commentEntity, urls);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findAllByPostId(Long postId) throws NotFoundException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시물에 대한 정보를 찾을 수 없습니다."));

        List<Comment> comments = commentRepository.findAllByPost(post);

        List<CommentResponse> commentResponses = comments.stream()
                .filter(comment -> comment.getDeletedAt() == null)
                .map(comment -> {
                    List<CommentPhoto> commentPhotos = comment.getCommentPhotos();
                    List<String> photoUrls = commentPhotos.stream().map(CommentPhoto::getUrl).collect(Collectors.toList());
                    return commentConverter.converterToCommentResponse(comment, photoUrls);
                })
                .collect(Collectors.toList());

        return commentResponses;
    }

    @Transactional
    public CommentResponse changeComment(List<MultipartFile> multipartFiles, CommentChangeRequest commentChangeRequest) throws IOException {
        // 요청에 대한 댓글 검색
        Comment comment = commentRepository.findById(commentChangeRequest.getCommentId()).orElseThrow(() -> new NoSuchElementException("댓글에 대한 정보를 찾을 수 없습니다."));

        // TODO: 사진 삭제 soft delete로 변경
        // 댓글에 관계된 사진들이랑 요청에 들어온 삭제 요청 사진 uri랑 같으면 해당 데이터 삭제
        List<CommentPhoto> commentPhotos = comment.getCommentPhotos();
        commentPhotos.forEach(commentPhoto -> {
            if (Objects.equals(commentPhoto.getCommentPhotoId(), commentChangeRequest.getCommentId())) {
                commentPhotoRepository.deleteById(commentPhoto.getCommentPhotoId());
            }
        });

        // 댓글 내용 수정
        comment.changedCommentContents(commentChangeRequest.getContents());

        // 새로 추가한 사진 s3에 업로드 하고 해당 파일들에 대한 url 리턴
        List<String> fileUrls = fileUpload(multipartFiles);

        // 새로 추가한 사진에 대한 commentPhoto 생성 후 db에 저장
        commentPhotoSave(comment, fileUrls);

        // comment에 대한 모든 사진 url
        List<String> urls = new ArrayList<>();

        // commentId로 commentPhoto 검색 후
        commentPhotoRepository.findAllByCommentId(comment.getId())
                .forEach(commentPhoto -> urls.add(commentPhoto.getUrl()));

        return commentConverter.converterToCommentResponse(comment, urls);
    }

    // 사진 정보에 대해서 전부 db에 저장한 후에 바로 커밋해야 전체 검색을 했을 때 저장된 정보 나옴
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void commentPhotoSave(Comment comment, List<String> fileUrls) {
        List<CommentPhoto> photos = fileUrls.stream()
                .map(url -> commentConverter.converterToCommentPhoto(comment, url))
                .collect(Collectors.toList());
        commentPhotoRepository.saveAll(photos);
    }

    private List<String> fileUpload(List<MultipartFile> multipartFiles) throws IOException {
        List<String> uploadUrls = new ArrayList<>();

        if (multipartFiles != null) {
            for (MultipartFile multipartFile : multipartFiles) {
                String upload = s3Uploader.upload(multipartFile, "comment-photo");
                uploadUrls.add(upload);
            }
        }

        return uploadUrls;
    }
}
