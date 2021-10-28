package com.kdt.localinfo.controller.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
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

}
