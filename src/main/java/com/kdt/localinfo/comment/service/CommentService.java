package com.kdt.localinfo.comment.service;

import com.kdt.localinfo.comment.converter.CommentConverter;
import com.kdt.localinfo.comment.dto.CommentSaveRequest;
import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.comment.repository.CommentRepository;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.post.repository.PostRepository;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentConverter commentConverter;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, CommentConverter commentConverter, UserRepository userRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.commentConverter = commentConverter;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public Long save(CommentSaveRequest commentSaveRequest) {
        Long postId = commentSaveRequest.getPostId();
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("게시물에 대한 정보를 찾을 수 없습니다."));

        Long userId = commentSaveRequest.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("작성자 정보를 찾을 수 없습니다."));

        Comment comment = commentConverter.converterToComment(commentSaveRequest, user, post);

        Comment commentEntity = commentRepository.save(comment);

        return commentEntity.getId();
    }
}
