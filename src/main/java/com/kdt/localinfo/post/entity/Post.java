package com.kdt.localinfo.post.entity;

import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.photo.Photo;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Table(name = "posts")
@NoArgsConstructor
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Lob
    @Column(name = "contents", nullable = false)
    private String contents;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Embedded
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToMany(mappedBy = "post")
    private List<Photo> photos = new ArrayList<>();

    @Builder
    public Post(String contents, Region region, Category category) {
        this.contents = contents;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deletedAt = null;
        this.region = region;
        this.category = category;
    }

    @Builder
    public Post(String contents, Region region, Category category, List<Photo> photos) {
        this.contents = contents;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deletedAt = null;
        this.region = region;
        this.category = category;
        this.photos = photos;
    }

    @Builder
    public Post(Long id, String contents, LocalDateTime createdAt, LocalDateTime updatedAt, Region region, Category category, List<Photo> photos) {
        this.id = id;
        this.contents = contents;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = null;
        this.region = region;
        this.category = category;
        this.photos = photos;
    }

    //연관관계 편의 메서드 - user
//    public void setUser(User user) {
//        if (Objects.nonNull(this.user)) {
//            this.user.getPosts().remove(this);
//        }
//
//        this.user = user;
//        user.getPosts().add(this);
//    }

    //연관관계 편의 메서드 - comment
//    public void addComment(Comment comment) {
//        comment.setPost(this);
//    }

    //연관관계 편의 메서드 - photo
    public void addPhoto(Photo photo) {
        photo.setPost(this);
    }

    //연관관계 편의 메서드 - category
    public void setCategory(Category category) {
        this.category = category;
    }

    public Long updatePost(String contents) {
        this.contents = contents;
        this.updatedAt = LocalDateTime.now();
        return id;
    }

    public Long deletePost() {
        this.deletedAt = LocalDateTime.now();
        return id;
    }

}
