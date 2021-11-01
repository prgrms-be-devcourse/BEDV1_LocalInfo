package com.kdt.localinfo.comment.service;

import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.category.CategoryRepository;
import com.kdt.localinfo.comment.dto.CommentResponse;
import com.kdt.localinfo.comment.dto.CommentSaveRequest;
import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.comment.repository.CommentRepository;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.post.repository.PostRepository;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@Slf4j
@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @Transactional
    @DisplayName("댓글 생성")
    void saveTest() throws NotFoundException {
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
        log.info("어디까지 실행되니??");
        Category category = new Category(1L, "동네생활");
        Category saveCategory = categoryRepository.save(category);
        Post post1 = Post.builder()
                .contents("this is sample post")
                .region(region)
                .category(saveCategory).build();
        post1.setUser(saveUser);
        log.info("어디까지 실행되니??2222");

        Post savePost = postRepository.save(post1);

        log.info("어디까지 실행되니??33333");
        CommentSaveRequest commentSaveRequest = new CommentSaveRequest(user.getId(), "안녕하세요 댓글입니다.");
        CommentResponse commentResponse = commentService.save(commentSaveRequest, savePost.getId());

        Comment comment = commentRepository.findById(commentResponse.getId()).orElseThrow();

        assertThat(comment.getId(), is(1L));
        assertThat(comment.getContents(), is("안녕하세요 댓글입니다."));
        assertThat(comment.getParentId(), is(nullValue()));
        assertThat(comment.getDeletedAt(), is(nullValue()));
        assertThat(comment.getUser(), is(user));
        assertThat(comment.getPost(), is(savePost));
        assertThat(commentResponse.getDepth(), is(0L));
        assertThat(commentResponse.getRegion(), is("행신동"));
        assertThat(commentResponse.getNickName(), is("nickname"));
    }
}
