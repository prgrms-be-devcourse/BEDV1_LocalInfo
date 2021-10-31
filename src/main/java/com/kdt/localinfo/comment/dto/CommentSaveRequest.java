package com.kdt.localinfo.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentSaveRequest {

    @NotNull
    private Long userId;
    @NotNull
    private String contents;
    private Long parentId;

    public CommentSaveRequest(Long userId, String contents) {
        this.userId = userId;
        this.contents = contents;
    }
}
