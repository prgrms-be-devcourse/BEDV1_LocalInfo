package com.kdt.localinfo.post.dto;

import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.photo.Photo;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.user.entity.User;
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
public class PostCreateRequest {

    private String contents;
    private Category category;
    private User user;
    private List<MultipartFile> photos;

    public Post toEntity(List<Photo> photos) {
        return Post.builder()
                .contents(contents)
                .region(user.getRegion())
                .category(category)
                .user(user)
                .photos(photos)
                .build();
    }
}