package com.kdt.localinfo.post.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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