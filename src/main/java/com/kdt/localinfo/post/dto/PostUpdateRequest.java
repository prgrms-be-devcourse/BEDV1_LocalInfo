package com.kdt.localinfo.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateRequest {

    private String postId;
    private String contents;
    private String categoryId;
    private List<MultipartFile> photos = new ArrayList<>();

}
