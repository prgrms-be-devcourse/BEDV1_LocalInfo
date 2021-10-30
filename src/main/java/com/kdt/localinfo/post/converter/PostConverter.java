package com.kdt.localinfo.post.converter;

import com.kdt.localinfo.post.dto.PostDto;
import com.kdt.localinfo.post.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostConverter {

    public Post convertToPost(PostDto postDto) {
        Post post = new Post(postDto.getId(), postDto.getContents(), postDto.getCreatedAt(), postDto.getUpdatedAt(),
                postDto.getRegion(), postDto.getCategory(), postDto.getPhotos());
        post.setCategory(postDto.getCategory());
        post.setUser(postDto.getUser());

        return post;
    }

    public PostDto convertToPostDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .contents(post.getContents())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .deletedAt(post.getDeletedAt())
                .region(post.getRegion())
                .category(post.getCategory())
                .photos(post.getPhotos())
                .build();
    }

}
