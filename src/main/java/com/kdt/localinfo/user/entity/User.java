package com.kdt.localinfo.user.entity;

import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.user.dto.UserRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(nullable = false, length = 30)
    private String email;

    @Column(nullable = false, length = 30)
    private String password;

    @CreatedDate
    @Column(name = "created_at", columnDefinition = "TIMESTAMP", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime deletedAt;

    @Embedded
    private Region region;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments;

    public User(UserRequest userRequest) {
        name = userRequest.getName();
        nickname = userRequest.getNickname();
        email = userRequest.getEmail();
        password = userRequest.getPassword();
        roles = setRole(userRequest);
        region = setRegion(userRequest);
        createdAt = LocalDateTime.now();
    }

    private Region setRegion(UserRequest userRequest) {
        return region = Region.builder()
                .neighborhood(userRequest.getNeighborhood())
                .district(userRequest.getDistrict())
                .city(userRequest.getCity())
                .build();
    }

    private Set<Role> setRole(UserRequest userRequest) {
        roles = new HashSet<>();
        roles.add(Role.valueOf(userRequest.getRole()));
        return roles;
    }

}

