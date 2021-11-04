package com.kdt.localinfo.comment.service;

import com.kdt.localinfo.aws.service.AwsS3Service;
import com.kdt.localinfo.comment.converter.CommentConverter;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

        return comments.stream()
                .filter(comment -> comment.getDeletedAt() == null)
                .map(commentConverter::converterToCommentResponse)
                .collect(Collectors.toList());
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
