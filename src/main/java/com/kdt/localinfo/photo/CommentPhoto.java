package com.kdt.localinfo.photo;

import com.kdt.localinfo.comment.entity.Comment;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class CommentPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_photo_id")
    private Long commentPhotoId;

    @Column(name = "url", nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "comment_id", foreignKey = @ForeignKey(name = "fk_photo_to_comment"))
    private Comment comment;

    public void setPost(Comment comment) {
        if (Objects.nonNull(this.comment)) {
            this.comment.getCommentPhotos().remove(this);
        }
        this.comment = comment;
        comment.getCommentPhotos().add(this);
    }
}
