package com.kdt.localinfo.photo;

import com.kdt.localinfo.post.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    private Post post;

    @Builder
    public Photo(String url) {
        this.url = url;
    }

    public void setPost(Post post) {
        if (Objects.nonNull(this.post)) {
            this.post.getPhotos().remove(this);
        }
        this.post = post;
        post.getPhotos().add(this);
    }
}
