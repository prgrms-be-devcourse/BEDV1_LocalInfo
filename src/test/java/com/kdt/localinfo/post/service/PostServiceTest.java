package com.kdt.localinfo.post.service;

import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.category.CategoryRepository;
import com.kdt.localinfo.post.dto.PostCreateRequest;
import com.kdt.localinfo.post.dto.PostResponse;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
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

    private PostCreateRequest postCreateRequest;

    @BeforeEach
    void setUp() throws IOException, NotFoundException {
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

        List<MultipartFile> multipartFiles = new ArrayList<>();

        postCreateRequest = PostCreateRequest.builder()
                .contents("this is sample post")
                .categoryId(String.valueOf(savedCategory1.getId()))
                .userId(String.valueOf(savedUser.getId()))
                .build();

        savedPostId = postService.savePost(postCreateRequest);
    }

    @Test
    @DisplayName("게시물 조회 내용 확인용 테스트")
    void findDetailPost() throws NotFoundException {
        PostResponse foundPost = postService.findDetailPost(savedPostId);
        assertThat(foundPost.getContents()).isEqualTo(postCreateRequest.getContents());
    }

    @Test
    @DisplayName("카테고리별 게시물 조회 내용 확인용 테스트")
    void findAllByCategory() {
        List<PostResponse> findPostsByCategory1 = postService.findAllByCategory(savedCategory1.getId());
        List<PostResponse> findPostsByCategory2 = postService.findAllByCategory(savedCategory2.getId());

        assertThat(findPostsByCategory1.size()).isEqualTo(1);
        assertThat(findPostsByCategory2.size()).isEqualTo(0);
    }
}