package com.kdt.localinfo.post.dto;

import com.kdt.localinfo.category.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostDto {

    private Long id;

    private String contents;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

//    private User user;

    private Category category;

    private LocalDateTime deletedAt;

    @Builder
    public PostDto(Long id, String contents, LocalDateTime createdAt, LocalDateTime updatedAt, Category category, LocalDateTime deletedAt) {
        this.id = id;
        this.contents = contents;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.category = category;
        this.deletedAt = deletedAt;
    }
}
