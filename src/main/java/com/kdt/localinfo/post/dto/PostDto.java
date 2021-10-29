package com.kdt.localinfo.post.dto;

import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.photo.Photo;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostDto {

    private Long id;

    private String contents;

    private Region region;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private Category category;

    private User user;

    private Comment comment;

    private List<Photo> photos;

    @Builder
    public PostDto(Long id, String contents, Region region, LocalDateTime createdAt, LocalDateTime updatedAt, Category category, LocalDateTime deletedAt, List<Photo> photos) {
        this.id = id;
        this.contents = contents;
        this.region = region;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.category = category;
        this.deletedAt = deletedAt;
        this.photos = photos;
    }
}
