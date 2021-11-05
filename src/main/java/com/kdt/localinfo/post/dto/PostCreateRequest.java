package com.kdt.localinfo.post.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateRequest {

    private String contents;
    private Long categoryId;
    private Long userId;

}