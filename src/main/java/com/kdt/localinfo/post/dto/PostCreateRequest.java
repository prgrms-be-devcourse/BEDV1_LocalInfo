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
    private String categoryId;
    private String userId;
    private List<MultipartFile> photos;


}