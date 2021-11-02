package com.kdt.localinfo.user.service;

import com.kdt.localinfo.user.dto.UserRequest;
import com.kdt.localinfo.user.dto.UserResponse;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.kdt.localinfo.user.entity.UserTest.getUserFixture;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;


@Slf4j
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("유저 생성 테스트 - 성공")
    void createUser() {
        User user = Mockito.mock(User.class);
        given(user.getId()).willReturn(1L);
        given(userRepository.save(any(User.class))).willReturn(user);

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
        then(userResponse).isNotNull();
    }

    @Test
    @DisplayName("유저 단건 조회 테스트 - 성공")
    void readUser() throws Exception {
        User user = getUserFixture();
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        UserResponse foundUser = userService.getUser(1L);
        then(foundUser)
                .hasFieldOrPropertyWithValue("id", user.getId())
                .hasFieldOrPropertyWithValue("name", user.getName())
                .hasFieldOrPropertyWithValue("nickname", user.getNickname())
                .hasFieldOrPropertyWithValue("email", user.getEmail())
                .hasFieldOrPropertyWithValue("password", user.getPassword())
                .hasFieldOrPropertyWithValue("roles", user.getRoles())
                .hasFieldOrPropertyWithValue("region", user.getRegion())
                .hasFieldOrPropertyWithValue("createdAt", user.getCreatedAt())
                .hasFieldOrPropertyWithValue("updatedAt", user.getUpdatedAt())
                .hasFieldOrPropertyWithValue("deletedAt", user.getDeletedAt());
    }

    @Test
    @DisplayName("유저 단건 조회 테스트 - 실패")
    void readUserNotFound() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());
        thenThrownBy(() -> userService.getUser(1L)).isExactlyInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("유저 전체 조회 테스트")
    void readAllUsers() throws Exception {
        List<User> users = Arrays.asList(
                getUserFixture(1L),
                getUserFixture(2L)
        );
        given(userRepository.findAll()).willReturn(users);

        List<UserResponse> userResponses = userService.getUserList();
        then(userResponses)
                .hasSize(2)
                .extracting("id")
                .contains(1L, 2L);
    }
}
