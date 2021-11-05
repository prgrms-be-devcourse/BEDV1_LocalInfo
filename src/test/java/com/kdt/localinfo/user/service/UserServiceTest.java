package com.kdt.localinfo.user.service;

import com.kdt.localinfo.user.dto.UserRequest;
import com.kdt.localinfo.user.dto.UserResponse;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.Role;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.kdt.localinfo.user.entity.UserTest.getUserFixture;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, modelMapper);
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
    void readUser() {
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
    @DisplayName("유저 단건 조회 테스트 - 실패: 없는 유저를 조회하는 경우")
    void readUserNotFound() {
        assertThatThrownBy(() -> userService.getUser(-1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 유저가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("유저 전체 조회 테스트")
    void readAllUsers() {
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

    @Test
    @DisplayName("유저 수정 테스트 - 실패: 없는 유저를 수정하려고 하는 경우")
    void updateNotFoundUser() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());
        thenThrownBy(() -> userService.updateUser(1L, new UserRequest()))
                .isExactlyInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("유저 삭제 테스트 - 실패: 없는 유저를 삭제하려고 하는 경우")
    void deleteNotFoundUser() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());
        thenThrownBy(() -> userService.deleteUser(1L))
                .isExactlyInstanceOf(EntityNotFoundException.class);
    }
}
