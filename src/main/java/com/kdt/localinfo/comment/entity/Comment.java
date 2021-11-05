package com.kdt.localinfo.comment.entity;

import com.kdt.localinfo.common.BaseEntity;
import com.kdt.localinfo.photo.CommentPhoto;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contents;

    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_comment_to_user"))
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "fk_comment_to_post"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    private Long parentId;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private final List<CommentPhoto> commentPhotos = new ArrayList<>();

    public void changedCommentContents(String contents){
        this.contents = contents;
    }

    public void setPost(Post post) {
        if (Objects.nonNull(this.post)) {
            this.post.getComments().remove(this);
        }
        this.post = post;
        post.getComments().add(this);
    }

    public void deletedComment(){
        this.deletedAt = LocalDateTime.now();
    }

    public void setUser(User user) {
        if (Objects.nonNull(this.user)) {
            user.getComments().remove(this);
        }
        this.user = user;
        user.getComments().add(this);
    }
}
