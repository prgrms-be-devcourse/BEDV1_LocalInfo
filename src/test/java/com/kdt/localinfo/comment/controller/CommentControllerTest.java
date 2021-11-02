package com.kdt.localinfo.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.category.CategoryRepository;
import com.kdt.localinfo.comment.dto.CommentSaveRequest;
import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.comment.repository.CommentRepository;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.post.repository.PostRepository;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("댓글 생성")
    void saveTest() throws Exception {
        Region region = Region.builder()
                .city("고양시")
                .district("덕양구")
                .neighborhood("행신동")
                .build();
        User user = User.builder()
                .email("email1")
                .region(region)
                .nickname("nickname")
                .password("password")
                .name("name")
                .build();
        User saveUser = userRepository.save(user);
        Category category = new Category(1L, "동네생활");
        Category saveCategory = categoryRepository.save(category);
        Post post1 = Post.builder()
                .contents("this is sample post")
                .region(region)
                .category(saveCategory).build();
        post1.setUser(saveUser);
        Post savePost = postRepository.save(post1);

        CommentSaveRequest commentSaveRequest = new CommentSaveRequest(saveUser.getId(), "댓글 생성해주세요.");
        mockMvc.perform(post("/posts/{post-id}/comments", savePost.getId())
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .contentType(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(commentSaveRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("_links.self").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 아이디로 댓글 조회")
    void findAllByPostIdTest() throws Exception {
        Region region = Region.builder()
                .city("고양시")
                .district("덕양구")
                .neighborhood("행신동")
                .build();
        User user = User.builder()
                .email("email1")
                .region(region)
                .nickname("nickname")
                .password("password")
                .name("name")
                .build();
        User saveUser = userRepository.save(user);
        Category category = new Category(1L, "동네생활");
        Category saveCategory = categoryRepository.save(category);
        Post post1 = Post.builder()
                .contents("this is sample post")
                .region(region)
                .category(saveCategory).build();
        post1.setUser(saveUser);
        Post savePost = postRepository.save(post1);

        Comment comment = Comment.builder()
                .contents("댓글")
                .build();
        comment.setPost(savePost);
        comment.setUser(saveUser);

        Comment comment2 = Comment.builder()
                .contents("댓글")
                .build();
        comment2.setPost(savePost);
        comment2.setUser(saveUser);

        commentRepository.save(comment);
        commentRepository.save(comment2);

        mockMvc.perform(get("/posts/{post-id}/comments", savePost.getId())
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andDo(print());
    }
}
