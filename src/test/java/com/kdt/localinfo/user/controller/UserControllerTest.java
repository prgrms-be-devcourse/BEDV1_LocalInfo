package com.kdt.localinfo.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Objects;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class UserControllerTest {

    private final String BASE_URL = "/users";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    public void createUser() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .name("?????????")
                .nickname("poogle")
                .email("suhyun@mail.com")
                .password("1234")
                .role("GENERAL")
                .neighborhood("?????????")
                .district("?????????")
                .city("?????????")
                .build();
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .contentType(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.delete").exists())
                .andExpect(jsonPath("_links.edit").exists())
                .andReturn();

        UserResponse userResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class);
        User savedUser = userRepository.findById(userResponse.getId()).orElse(null);

        assertAll(
                () -> assertEquals(userResponse.getName(), userRequest.getName()),
                () -> assertEquals(Objects.requireNonNull(savedUser).getName(), userRequest.getName())
        );
    }

    @Test
    @DisplayName("?????? ????????? ??????")
    public void getUsers() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    public void getUser() throws Exception {
        Region region = Region.builder()
                .neighborhood("?????????")
                .district("?????????")
                .city("?????????")
                .build();
        User user = User.builder()
                .name("?????????")
                .nickname("poogle")
                .email("suhyun@mail.com")
                .password("1234")
                .roles(Set.of(Role.valueOf("GENERAL")))
                .region(region)
                .build();
        User savedUser = userRepository.save(user);

        mockMvc.perform(get(BASE_URL + "/{id}", savedUser.getId())
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("?????? ??????")
    public void editUser() throws Exception {
        Region region = Region.builder()
                .neighborhood("?????????")
                .district("?????????")
                .city("?????????")
                .build();
        User user = User.builder()
                .name("?????????")
                .nickname("poogle")
                .email("suhyun@mail.com")
                .password("1234")
                .roles(Set.of(Role.valueOf("GENERAL")))
                .region(region)
                .build();
        User savedUser = userRepository.save(user);

        UserRequest userRequest = UserRequest.builder()
                .name("?????????")
                .nickname("pogle")
                .email("su@mail.com")
                .password("1111")
                .role("ADMIN")
                .neighborhood("?????????")
                .district("?????????")
                .city("?????????")
                .build();

        mockMvc.perform(put(BASE_URL + "/{id}", savedUser.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(userRequest)));
    }

    @Test
    @DisplayName("?????? ??????")
    public void deleteUser() throws Exception {
        Region region = Region.builder()
                .neighborhood("?????????")
                .district("?????????")
                .city("?????????")
                .build();
        User user = User.builder()
                .name("?????????")
                .nickname("poogle")
                .email("suhyun@mail.com")
                .password("1234")
                .roles(Set.of(Role.valueOf("GENERAL")))
                .region(region)
                .build();
        User savedUser = userRepository.save(user);
        log.info("[*] savedUser:{}", savedUser);
        mockMvc.perform(delete(BASE_URL + "/{id}", savedUser.getId())
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNoContent());
        User deletedUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(deletedUser.getDeletedAt(), is(notNullValue()));
    }
}
