package com.kdt.localinfo.post.service;

import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.category.CategoryRepository;
import com.kdt.localinfo.post.dto.PostCreateRequest;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.io.IOException;

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
    void setUp() throws IOException {
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

        postCreateRequest = PostCreateRequest.builder()
                .contents("this is sample post")
                .category(savedCategory1)
                .user(savedUser)
                .build();

        savedPostId = postService.createPost(postCreateRequest);
    }

}