package com.kdt.localinfo.comment.service;

import com.kdt.localinfo.comment.dto.CommentResponse;
import com.kdt.localinfo.comment.dto.CommentSaveRequest;
import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.comment.repository.CommentRepository;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.post.repository.PostRepository;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import javassist.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest
@Sql(scripts = {"classpath:data.sql"})
class CommentServiceTest {

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    @Test
    @Transactional
    @DisplayName("댓글 생성")
    void saveTest() throws NotFoundException {
        User user = userRepository.findById(1L).orElseThrow();
        Post post = postRepository.findById(1L).orElseThrow();

        CommentSaveRequest commentSaveRequest = new CommentSaveRequest(user.getId(), "안녕하세요 댓글입니다.");
        CommentResponse commentResponse = commentService.save(commentSaveRequest, post.getId());

        Comment comment = commentRepository.findById(commentResponse.getId()).orElseThrow();

        assertThat(comment.getId(), is(1L));
        assertThat(comment.getContents(), is("안녕하세요 댓글입니다."));
        assertThat(comment.getParentId(), is(nullValue()));
        assertThat(comment.getDeletedAt(), is(nullValue()));
        assertThat(comment.getUser(), is(user));
        assertThat(comment.getPost(), is(post));
        assertThat(commentResponse.getDepth(), is(0L));
        assertThat(commentResponse.getRegion(), is("행신동"));
        assertThat(commentResponse.getNickName(), is("0kwon"));
    }
}