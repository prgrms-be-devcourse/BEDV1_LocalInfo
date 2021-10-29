package com.kdt.localinfo.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentSaveRequest {
    private Long userId;
    private String contents;
    private Long parentId;

    public CommentSaveRequest(Long userId, String contents) {
        this.userId = userId;
        this.contents = contents;
    }
}
