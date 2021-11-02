package com.kdt.localinfo.user.service;

import com.kdt.localinfo.user.dto.UserRequest;
import com.kdt.localinfo.user.dto.UserResponse;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Autowired
    private UserService userService;
    private Region region = Region.builder()
            .neighborhood("neighbor")
            .district("district")
            .city("city")
            .build();

    @Test
    @DisplayName("유저 생성 테스트")
    void createUser() {
        UserRequest userRequest = UserRequest.builder()
                .name("name")
                .nickname("testNickname")
                .email("test@mail.com")
                .password("testPassword")
                .role("GENERAL")
                .neighborhood("neighbor")
                .district("district")
                .city("city")
                .build();
        UserResponse userResponse = userService.addUser(userRequest);
        assertAll(
                () -> assertEquals(userResponse.getName(), "name"),
                () -> assertEquals(userResponse.getNickname(), "testNickname"),
                () -> assertEquals(userResponse.getEmail(), "test@mail.com"),
                () -> assertEquals(userResponse.getPassword(), "testPassword"),
                () -> assertEquals(userResponse.getRoles(), Set.of(Role.GENERAL)),
                () -> assertEquals(userResponse.getRegion(), region),
                () -> assertThat(userResponse.getCreatedAt()).isNotNull(),
                () -> assertThat(userResponse.getUpdatedAt()).isAfterOrEqualTo(userResponse.getCreatedAt()),
                () -> assertThat(userResponse.getDeletedAt()).isNull()
        );
    }
}
