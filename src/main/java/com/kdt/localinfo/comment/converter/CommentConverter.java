package com.kdt.localinfo.comment.converter;

import com.kdt.localinfo.comment.dto.CommentSaveRequest;
import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class CommentConverter {
    public Comment converterToComment(CommentSaveRequest commentSaveRequest, User user, Post post) {
        Comment comment = Comment.builder()
                .contents(commentSaveRequest.getContents())
                .parentId(commentSaveRequest.getParentCommentId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        comment.setPost(post);
        comment.setUser(user);
        return comment;
    }
}