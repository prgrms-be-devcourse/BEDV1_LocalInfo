package com.kdt.localinfo.comment.service;

import com.kdt.localinfo.comment.converter.CommentConverter;
import com.kdt.localinfo.comment.dto.CommentDepth;
import com.kdt.localinfo.comment.dto.CommentResponse;
import com.kdt.localinfo.comment.dto.CommentSaveRequest;
import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.comment.repository.CommentRepository;
import com.kdt.localinfo.common.TestEntityFactory;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.post.repository.PostRepository;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import javassist.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentConverter commentConverter;

    @Test
    @Transactional
    @DisplayName("댓글 생성")
    void saveTest() throws NotFoundException {
        // GIVEN
        Comment comment = TestEntityFactory.commentBuilder().build();
        User user = comment.getUser();
        Post post = comment.getPost();

        CommentSaveRequest commentSaveRequest = new CommentSaveRequest(user.getId(), "안녕하세요 댓글입니다.");

        Comment expectComment = commentConverter.converterToComment(commentSaveRequest, user, post);

        CommentResponse expectCommentResponse = new CommentResponse(comment.getId(),
                "안녕하세요 댓글입니다.",
                user.getNickname(),
                LocalDateTime.now(),
                user.getRegion().getNeighborhood(),
                null,
                checkedDepth(comment.getParentId()));

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
        given(commentConverter.converterToComment(commentSaveRequest, user, post)).willReturn(expectComment);
        given(commentConverter.converterToCommentResponse(expectComment)).willReturn(expectCommentResponse);

        // WHEN
        CommentResponse commentResponse = commentService.save(commentSaveRequest, post.getId());

        // THEN
        assertThat(commentResponse.getId(), is(expectCommentResponse.getId()));
        assertThat(commentResponse.getContents(), is(expectCommentResponse.getContents()));
        assertThat(commentResponse.getParentId(), is(expectCommentResponse.getParentId()));
        assertThat(commentResponse.getDepth(), is(expectCommentResponse.getDepth()));
        assertThat(commentResponse.getRegion(), is(expectCommentResponse.getRegion()));
        assertThat(commentResponse.getNickName(), is(expectCommentResponse.getNickName()));
    }

    @Test
    @DisplayName("게시글 아이디로 댓글 조회 Service")
    void findAllByPostIdTest() throws NotFoundException {

        // GIVEN
        Comment comment = TestEntityFactory.commentBuilder().build();

        CommentResponse commentResponse = new CommentResponse(comment.getId(),
                comment.getContents(),
                comment.getUser().getNickname(),
                comment.getUpdatedAt(),
                comment.getUser().getRegion().getNeighborhood(),
                comment.getParentId(),
                checkedDepth(comment.getParentId()));

        List<Comment> returnComments = new ArrayList<>();
        returnComments.add(comment);
        returnComments.add(comment);

        Post post = comment.getPost();
        Long postId = post.getId();

        given(commentRepository.findAllByPost(post)).willReturn(returnComments);
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(commentConverter.converterToCommentResponse(comment)).willReturn(commentResponse);

        // WHEN
        List<CommentResponse> commentResponses = commentService.findAllByPostId(postId);

        // THEN
        then(postRepository).should().findById(postId);
        then(commentRepository).should().findAllByPost(post);
        then(commentConverter).should(times(2)).converterToCommentResponse(comment);

        assertThat(commentResponses.size(), is(2));
        commentResponses.forEach(commentResponse1 -> {
            assertThat(commentResponse1.getNickName(), is("nickName"));
            assertThat(commentResponse1.getDepth(), is(0L));
            assertThat(commentResponse1.getContents(), is("댓글"));
        });
    }

    private Long checkedDepth(Long parentId) {
        return parentId == null ? CommentDepth.ZERO.getDepth() : CommentDepth.ONE.getDepth();
    }
}
