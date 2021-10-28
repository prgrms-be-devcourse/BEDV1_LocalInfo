package com.kdt.localinfo.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String contents;
    private String nickName;
    private LocalDateTime lastUpdatedAt;
    private String region;
    private Long parentId;
    private Long depth;
}
