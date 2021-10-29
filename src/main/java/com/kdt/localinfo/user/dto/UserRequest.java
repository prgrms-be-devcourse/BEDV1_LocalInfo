package com.kdt.localinfo.user.dto;

import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.Role;
import com.kdt.localinfo.user.entity.User;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class UserRequest {
    @NotEmpty
    private String name;
    @NotEmpty
    private String nickname;
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    private String password;
//    @NotEmpty
    private String role;
    @NotEmpty
    private String neighborhood;
    @NotEmpty
    private String district;
    @NotEmpty
    private String city;

    public User toEntity() {
        return User.builder()
                .name(name)
                .nickname(nickname)
                .email(email)
                .password(password)
//                .roles(setRole(role))
                .region(setRegion(neighborhood, district, city))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Region setRegion(String neighborhood, String district, String city) {
        return Region.builder()
                .neighborhood(neighborhood)
                .district(district)
                .city(city)
                .build();
    }

    //TODO: 유저가 존재할 때 찾아서 업데이트
    private Set<Role> setRole(String role) {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.valueOf(role));
        return roles;
    }

}
