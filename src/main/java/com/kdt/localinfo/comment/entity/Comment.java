package com.kdt.localinfo.comment.entity;

import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    private Long parentId;

    public void setPost(Post post) {
        if (Objects.nonNull(this.post)) {
            this.post.getComments().remove(this);
        }
        this.post = post;
        post.getComments().add(this);
    }

    public void setUser(User user) {
        if (Objects.nonNull(this.user)) {
            user.getComments().remove(this);
        }
        this.user = user;
        user.getComments().add(this);
    }
}
