package com.kdt.localinfo.controller.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRequest {
    @NotNull
    private String name;
    @NotNull
    private String nickname;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String role;
    @NotNull
    private String neighborhood;
    @NotNull
    private String district;
    @NotNull
    private String city;

}
