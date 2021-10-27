package com.kdt.localinfo.user.entity;

import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "users")
public class User {
    @OneToMany(mappedBy = "user")
    private final List<Post> posts = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private final List<Comment> comments = new ArrayList<>();

    @Id
    @GeneratedValue(generator = "USER_SEQ_ID")

    private Long id;
    private String name;
    private String nickName;
    private String email;
    private String password;

    @OneToOne
    private Region region;
    @OneToOne
    private Role role;

    private LocalDateTime createAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
