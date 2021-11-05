package com.kdt.localinfo.photo;

import com.kdt.localinfo.post.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    @JoinColumn(name = "post_id", referencedColumnName = "post_id", foreignKey = @ForeignKey(name = "fk_photo_to_post"))
    private Post post;

    @Builder
    public Photo(String url) {
        this.url = url;
    }

}
