package com.kdt.localinfo.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentSaveRequest {
    private Long userId;
    private Long postId;
    private String contents;
    private Long parentId;

    public CommentSaveRequest(Long userId, Long postId, String contents) {
        this.userId = userId;
        this.postId = postId;
        this.contents = contents;
    }
}
