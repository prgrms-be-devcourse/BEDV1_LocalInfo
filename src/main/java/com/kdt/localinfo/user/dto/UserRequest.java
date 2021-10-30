package com.kdt.localinfo.user.dto;

import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.Role;
import com.kdt.localinfo.user.entity.User;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
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
    @NotEmpty
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
                .roles(Set.of(Role.valueOf(this.role)))
                .region(setRegion(neighborhood, district, city))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Region setRegion(String neighborhood, String district, String city) {
        return Region.builder()
                .neighborhood(neighborhood)
                .district(district)
                .city(city)
                .build();
    }

}
