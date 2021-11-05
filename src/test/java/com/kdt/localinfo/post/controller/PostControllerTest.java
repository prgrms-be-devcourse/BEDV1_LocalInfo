package com.kdt.localinfo.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.category.CategoryRepository;
import com.kdt.localinfo.post.dto.PostCreateRequest;
import com.kdt.localinfo.post.service.PostService;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostService postService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private PostCreateRequest postCreateRequest;

    private Long savedPostId;

    private User savedUser;

    private Category savedCategory1;

    private Category savedCategory2;

    @BeforeEach
    void saveSampleData() throws IOException, NotFoundException {
        Category category = new Category(1L, "동네생활");
        Category category2 = new Category(2L, "동네맛집");
        savedCategory1 = categoryRepository.save(category);
        savedCategory2 = categoryRepository.save(category2);

        Region region = Region.builder()
                .city("city1")
                .district("district1")
                .neighborhood("neighborhood1")
                .build();
        User user = User.builder()
                .email("email1")
                .region(region)
                .nickname("nickname")
                .password("password")
                .name("name")
                .build();
        savedUser = userRepository.save(user);

        List<MultipartFile> multipartFiles = new ArrayList<>();
        postCreateRequest = PostCreateRequest.builder()
                .contents("this is sample post")
                .categoryId(String.valueOf(savedCategory1.getId()))
                .userId(String.valueOf(savedUser.getId()))
                .build();

        savedPostId = postService.savePost(postCreateRequest);
    }

//    @Test
//    @DisplayName("게시물 작성 테스트")
//    void write() throws Exception {
//        mockMvc.perform(post("/posts")
//                        .accept(MediaTypes.HAL_JSON_VALUE)
//                        .contentType(MediaTypes.HAL_JSON_VALUE)
//                        .content(objectMapper.writeValueAsString(postCreateRequest))
//                )
//                .andExpect(status().isCreated())
//                .andDo(print());
//    }

    @Test
    @DisplayName("게시물 상세 조회 테스트")
    void findDetailPost() throws Exception {
        mockMvc.perform(get("/posts/{post-id}", savedPostId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("카테고리별 게시물 조회 테스트")
    void findPostByCategory() throws Exception {
        mockMvc.perform(get("/posts/categories/{category-id}", savedCategory1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
