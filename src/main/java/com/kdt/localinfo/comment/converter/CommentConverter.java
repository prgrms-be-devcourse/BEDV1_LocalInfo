package com.kdt.localinfo.comment.converter;

import com.kdt.localinfo.comment.dto.CommentDepth;
import com.kdt.localinfo.comment.dto.CommentResponse;
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
                .parentId(commentSaveRequest.getParentId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        comment.setPost(post);
        comment.setUser(user);
        return comment;
    }

    public CommentResponse converterToCommentResponse(Comment comment) {
        return new CommentResponse(comment.getId(), comment.getContents(), comment.getUser().getNickName(), comment.getUpdatedAt(), comment.getUser().getRegion().getName(), comment.getParentId(), checkedCommentDepth(comment.getParentId()));
    }

    private Long checkedCommentDepth(Long parentId) {
        if (parentId.equals(0L)) {
            return CommentDepth.ZERO.getDepth();
        }
        return CommentDepth.ONE.getDepth();
    }
}
