package com.kdt.localinfo.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentChangeRequest {
    @NotNull
    private Long commentId;
    private String contents;
    private Long deletedPhotoId;
}
