package com.kdt.localinfo.post.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.common.BaseEntity;
import com.kdt.localinfo.photo.Photo;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ToString
@Getter
@Table(name = "posts")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Entity
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "post_id")
    private Long id;

    @Lob
    @Column(name = "contents", nullable = false)
    private String contents;

    @Embedded
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_post_to_category"))
    private Category category;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_post_to_user"))
    private User user;

    @OneToMany(mappedBy = "post")
    private List<Photo> photos = new ArrayList<>();

    //Create요청시 사용하는 builder
    @Builder
    public Post(String contents, User user, List<Photo> photos, Category category) {
        this.contents = contents;
        this.region = user.getRegion();
        setUser(user);
        this.photos = photos;
        this.category = category;
        setCategory(category);
    }

    @Builder
    public Post(Long id, String contents, Category category, Region region) {
        this.id = id;
        this.contents = contents;
        this.category = category;
        this.region = region;
    }

    public Post(String contents, Region region, Category category) {
        this.contents = contents;
        this.region = region;
        setCategory(category);
    }

    public Long updatePost(String contents, Category category, List<Photo> photos) {
        this.contents = contents;
        this.category = category;
        setCategory(category);
        this.photos = photos;
        return id;
    }

    public Long deletePost() {
        deletedAt = LocalDateTime.now();
        return id;
    }

    public void addComment(Comment comment) {
        comment.setPost(this);
    }

    public void setUser(User user) {
        if (Objects.nonNull(this.user)) {
            this.user.getPosts().remove(this);
        }
        this.user = user;
        user.getPosts().add(this);
    }

    public void addPhoto(List<Photo> photos) {
        this.photos = photos;
    }

    public void setComments(List<Comment> comments) {
        comments.forEach(this::addComment);
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

}
