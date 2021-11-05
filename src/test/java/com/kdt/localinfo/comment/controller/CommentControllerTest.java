package com.kdt.localinfo.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.category.CategoryRepository;
import com.kdt.localinfo.comment.dto.CommentChangeRequest;
import com.kdt.localinfo.comment.dto.CommentSaveRequest;
import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.comment.repository.CommentRepository;
import com.kdt.localinfo.photo.CommentPhoto;
import com.kdt.localinfo.photo.CommentPhotoRepository;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.post.repository.PostRepository;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
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

    @Autowired
    private CommentPhotoRepository commentPhotoRepository;

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

        File imageFile = new File(System.getProperty("user.dir") + "/comment-photo/test.jpg");
        File imageFile2 = new File(System.getProperty("user.dir") + "/comment-photo/test2.jpg");
        MockMultipartFile firstFile = new MockMultipartFile("images", "test.jpg", null, Files.readAllBytes(imageFile.toPath()));
        MockMultipartFile secondFile = new MockMultipartFile("images", "test2.jpg", null, Files.readAllBytes(imageFile2.toPath()));

        mockMvc.perform(multipart("/posts/{post-id}/comments", savePost.getId())
                        .file(firstFile)
                        .file(secondFile)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .contentType(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(commentSaveRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.findAllByPostId").exists())
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
                .contents("댓글2")
                .build();
        comment2.setPost(savePost);
        comment2.setUser(saveUser);
        comment.setDeleted();
        commentRepository.save(comment);
        commentRepository.save(comment2);

        String photoUrl = "https://localinfo-photo.s3.ap-northeast-2.amazonaws.com/comment-photo/f63e66a8-9840-4284-914a-e6c69ed48fef-test.jpg";
        CommentPhoto commentPhoto = new CommentPhoto(photoUrl, comment2);
        commentPhotoRepository.save(commentPhoto);

        mockMvc.perform(get("/posts/{post-id}/comments", savePost.getId())
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.commentResponseList..contents").value("댓글2"))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정")
    void changedCommentTest() throws Exception {
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
        Comment savedComment = commentRepository.save(comment);

        String photoUrl = "https://localinfo-photo.s3.ap-northeast-2.amazonaws.com/comment-photo/01806ddd-0ff8-4152-a156-e2c2d9b4050c-test.jpg";
        CommentPhoto commentPhoto = new CommentPhoto(photoUrl, comment);
        CommentPhoto savedPhoto = commentPhotoRepository.save(commentPhoto);

        CommentChangeRequest commentChangeRequest = new CommentChangeRequest(
                savedComment.getId(),
                "수정된 내용",
                savedPhoto.getCommentPhotoId());

        File imageFile = new File(System.getProperty("user.dir") + "/comment-photo/changeTest.jpg");
        MockMultipartFile updateFile = new MockMultipartFile("images", "changeTest.jpg", null, Files.readAllBytes(imageFile.toPath()));

        mockMvc.perform(multipart("/posts/comments")
                        .file(updateFile)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .contentType(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(commentChangeRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andDo(print());
    }
}
