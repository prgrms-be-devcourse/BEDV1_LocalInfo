package com.kdt.localinfo.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.Role;
import com.kdt.localinfo.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String nickname;
    private String email;
    private String password;
    private Set<Role> roles;
    private Region region;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.roles = user.getRoles();
        this.region = user.getRegion();
        this.createdAt = user.getCreatedAt();
    }
}
