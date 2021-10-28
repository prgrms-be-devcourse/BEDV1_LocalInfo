package com.kdt.localinfo.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.localinfo.model.User;
import com.kdt.localinfo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private final String BASE_URL = "/api/users";

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
    @DisplayName("정상적으로 사용자 생성하는 테스트")
    public void createUser() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .name("심수현")
                .nickname("poogle")
                .email("suhyun@mail.com")
                .password("1234")
                .role("GENERAL")
                .neighborhood("동천동")
                .district("수지구")
                .city("용인시")
                .build();
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("roles").exists())
                .andExpect(jsonPath("region").exists())
                .andReturn();
        UserResponse userResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class);
        User savedUser = userRepository.findById(userResponse.getId()).orElse(null);
        assertAll(
                () -> assertEquals(userResponse.getName(), userRequest.getName()),
                () -> assertEquals(Objects.requireNonNull(savedUser).getName(), userRequest.getName())
        );
    }
}
