package com.kdt.localinfo.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.category.CategoryRepository;
import com.kdt.localinfo.photo.Photo;
import com.kdt.localinfo.post.dto.PostDto;
import com.kdt.localinfo.post.service.PostService;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
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

    private PostDto postDto;

    private Long savedPostId;

    private User savedUser;

    private Category savedCategory1;

    private Category savedCategory2;

    @BeforeEach
    void saveSampleData() {
        List<Photo> photos = new ArrayList<>();
        Photo photo1 = new Photo("url1");
        Photo photo2 = new Photo("url2");
        photos.add(photo1);
        photos.add(photo2);

        Category category = new Category(1L, "동네생활");
        savedCategory1 = categoryRepository.save(category);

        Category category2 = new Category(2L, "동네맛집");
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

        postDto = PostDto.builder()
                .contents("this is sample post")
                .region(region)
                .category(savedCategory1)
                .photos(photos)
                .user(user)
                .build();

        savedPostId = postService.createPost(postDto);
    }

    @Test
    @DisplayName("게시물 작성 테스트")
    void write() throws Exception {
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andDo(print());
    }

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
        mockMvc.perform(get("/categories/{category-id}", savedCategory1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시물 수정 테스트")
    void updatePost() throws Exception {
        List<Photo> photos = new ArrayList<>();
        Photo photo1 = new Photo("updated url1");
        Photo photo2 = new Photo("updated url2");
        photos.add(photo1);
        photos.add(photo2);

        PostDto newPostDto = PostDto.builder()
                .id(savedPostId)
                .contents("this is updated post")
                .region(savedUser.getRegion())
                .user(savedUser)
                .category(savedCategory2)
                .photos(photos)
                .build();
        mockMvc.perform(put("/posts/{id}", newPostDto.getId())
                .content(objectMapper.writeValueAsString(newPostDto))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시물 삭제 테스트")
    void deletePost() throws Exception {
        mockMvc.perform(delete("/posts/{post-id}", savedPostId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

}
