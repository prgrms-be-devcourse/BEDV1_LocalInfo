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
                .orElseThrow(() -> new NotFoundException("???????????? ?????? ????????? ?????? ??? ????????????."));

        Long userId = commentSaveRequest.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("????????? ????????? ?????? ??? ????????????."));

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
                .orElseThrow(() -> new NotFoundException("???????????? ?????? ????????? ?????? ??? ????????????."));

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
        // ????????? ?????? ?????? ??????
        Comment comment = commentRepository.findById(commentChangeRequest.getCommentId()).orElseThrow(() -> new NoSuchElementException("????????? ?????? ????????? ?????? ??? ????????????."));

        // TODO: ?????? ?????? soft delete??? ??????
        // ????????? ????????? ??????????????? ????????? ????????? ?????? ?????? ?????? uri??? ????????? ?????? ????????? ??????
        List<CommentPhoto> commentPhotos = comment.getCommentPhotos();
        commentPhotos.forEach(commentPhoto -> {
            if (Objects.equals(commentPhoto.getCommentPhotoId(), commentChangeRequest.getCommentId())) {
                commentPhotoRepository.deleteById(commentPhoto.getCommentPhotoId());
            }
        });

        // ?????? ?????? ??????
        comment.changedCommentContents(commentChangeRequest.getContents());

        // ?????? ????????? ?????? s3??? ????????? ?????? ?????? ???????????? ?????? url ??????
        List<String> fileUrls = fileUpload(multipartFiles);

        // ?????? ????????? ????????? ?????? commentPhoto ?????? ??? db??? ??????
        commentPhotoSave(comment, fileUrls);

        // comment??? ?????? ?????? ?????? url
        List<String> urls = new ArrayList<>();

        // commentId??? commentPhoto ?????? ???
        commentPhotoRepository.findAllByCommentId(comment.getId())
                .forEach(commentPhoto -> urls.add(commentPhoto.getUrl()));

        return commentConverter.converterToCommentResponse(comment, urls);
    }

    @Transactional
    public void deleteComment(Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NoSuchElementException("?????? ???????????? ???????????? ????????? ?????? ??? ????????????."));
        comment.deletedComment();

        List<CommentPhoto> commentPhotos = comment.getCommentPhotos();
        commentPhotos.forEach(CommentPhoto::deleteCommentPhoto);
    }


    // ?????? ????????? ????????? ?????? db??? ????????? ?????? ?????? ???????????? ?????? ????????? ?????? ??? ????????? ?????? ??????
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
