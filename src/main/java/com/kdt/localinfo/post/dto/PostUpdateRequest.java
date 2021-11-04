package com.kdt.localinfo.post.dto;

import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.photo.Photo;
import com.kdt.localinfo.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateRequest {

    private String contents;
    private Long categoryId;

}
