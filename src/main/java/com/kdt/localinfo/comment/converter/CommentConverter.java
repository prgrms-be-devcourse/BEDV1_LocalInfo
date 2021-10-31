package com.kdt.localinfo.comment.converter;

import com.kdt.localinfo.comment.dto.CommentDepth;
import com.kdt.localinfo.comment.dto.CommentResponse;
import com.kdt.localinfo.comment.dto.CommentSaveRequest;
import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CommentConverter {

    public Comment converterToComment(CommentSaveRequest commentSaveRequest, User user, Post post) {
        Comment comment = Comment.builder()
                .contents(commentSaveRequest.getContents())
                .parentId(commentSaveRequest.getParentId())
                .build();
        comment.setPost(post);
        comment.setUser(user);
        return comment;
    }

    public CommentResponse converterToCommentResponse(Comment comment) {
        return new CommentResponse(comment.getId(),
                comment.getContents(),
                comment.getUser().getNickname(),
                comment.getUpdatedAt(),
                comment.getUser().getRegion().getNeighborhood(),
                comment.getParentId(),
                checkedCommentDepth(comment.getParentId()));
    }

    private Long checkedCommentDepth(Long parentId) {
        return parentId == null ? CommentDepth.ZERO.getDepth() : CommentDepth.ONE.getDepth();
    }
}
