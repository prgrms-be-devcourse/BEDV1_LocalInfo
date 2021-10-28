package com.kdt.localinfo.post.Entity;

import com.kdt.localinfo.category.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

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

//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "user_id", referencedColumnName = "id")
//    private User user;
//
//    @OneToMany(mappedBy = "comments")
//    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public Post(String contents, Category category) {
        this.contents = contents;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
//        this.comments = new ArrayList<Comment>();
        this.category = category;
        this.deletedAt = null;
    }

    @Builder
    public Post(Long id, String contents, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.contents = contents;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = null;
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
