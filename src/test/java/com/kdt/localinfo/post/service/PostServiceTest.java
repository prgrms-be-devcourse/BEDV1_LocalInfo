package com.kdt.localinfo.post.service;

import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.category.CategoryRepository;
import com.kdt.localinfo.photo.Photo;
import com.kdt.localinfo.post.dto.PostDto;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import javassist.NotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Long savedPostId;

    private User savedUser;

    private Category savedCategory1;

    private Category savedCategory2;

    private PostDto postDto;

    @BeforeEach
    void setUp() {
        List<Photo> photos = new ArrayList<>();
        Photo photo1 = new Photo("url1");
        Photo photo2 = new Photo("url2");
        photos.add(photo1);
        photos.add(photo2);

        Category category1 = new Category(1L, "동네생활");
        savedCategory1 = categoryRepository.save(category1);

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
    @DisplayName("게시물 조회 내용 확인용 테스트")
    void findDetailPost() throws NotFoundException {
        PostDto foundPostDto = postService.findDetailPost(savedPostId);
        assertThat(foundPostDto.getContents()).isEqualTo(postDto.getContents());
    }

    @Test
    @DisplayName("카테고리별 게시물 조회 내용 확인용 테스트")
    void findAllByCategory() {
        List<PostDto> findPostsByCategory1 = postService.findAllByCategory(savedCategory1.getId());
        List<PostDto> findPostsByCategory2 = postService.findAllByCategory(savedCategory2.getId());

        assertThat(findPostsByCategory1.size()).isEqualTo(1);
        assertThat(findPostsByCategory2.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("게시물 수정 내용 확인용 테스트")
    void updatePost() throws NotFoundException {
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

        Long updatedPostId = postService.updatePost(newPostDto.getId(), newPostDto);
        PostDto updatedPostDto = postService.findDetailPost(updatedPostId);

        assertThat(updatedPostDto.getUpdatedAt()).isNotEqualTo(postDto.getCreatedAt());
        assertThat(updatedPostDto.getContents()).isNotEqualTo(postDto.getContents());
        assertThat(updatedPostDto.getCategory().getName()).isNotEqualTo(postDto.getCategory().getName());
        assertThat(updatedPostDto.getPhotos().get(0).getUrl()).isNotEqualTo(postDto.getPhotos().get(0).getUrl());
    }
}