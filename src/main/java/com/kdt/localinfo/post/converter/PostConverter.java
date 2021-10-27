package com.kdt.localinfo.post.converter;

import com.kdt.localinfo.post.Entity.Post;
import com.kdt.localinfo.post.dto.PostDto;
import org.springframework.stereotype.Component;

@Component
public class PostConverter {

    public Post convertToPost(PostDto postDto) {
        Post post = new Post(postDto.getId(), postDto.getContents(), postDto.getCreatedAt(), postDto.getUpdatedAt());
//        post.setUser(postDto.getUser());
        post.setCategory(postDto.getCategory());
        return post;
    }

    public PostDto convertToPostDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .contents(post.getContents())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .category(post.getCategory())
                .deletedAt(post.getDeletedAt())
                .build();
    }

}
