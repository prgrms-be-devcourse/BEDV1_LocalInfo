package com.kdt.localinfo.post.dto;

import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.photo.Photo;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private Long id;
    private String contents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String category;
    private String user;
    private List<Photo> photos;
    private List<Comment> comments;

    public static PostResponse of(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .contents(post.getContents())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .category(post.getCategory().getName())
                .user(post.getUser().getName())
                .photos(post.getPhotos())
                .comments(post.getComments())
                .build();
    }


    public static List<PostResponse> of(List<Post> posts) {
        return posts.stream().map(PostResponse::of).collect(Collectors.toList());
    }
}
