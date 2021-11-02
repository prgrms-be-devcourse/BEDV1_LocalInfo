package com.kdt.localinfo.comment.service;

import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.comment.converter.CommentConverter;
import com.kdt.localinfo.comment.dto.CommentResponse;
import com.kdt.localinfo.comment.dto.CommentSaveRequest;
import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.comment.repository.CommentRepository;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.post.repository.PostRepository;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.Role;
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
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
        Region region = Region.builder().city("고양시").district("덕양구").neighborhood("행신동").build();

        User user = User.builder()
                .id(1L)
                .name("choi")
                .password("1234")
                .email("test@gmail.com")
                .nickname("0kwon")
                .region(region)
                .roles(Set.of(Role.GENERAL))
                .build();

        Post post = Post.builder()
                .id(1L)
                .category(new Category(1L, "동네 맛집"))
                .region(region)
                .contents("테스트 게시글")
                .build();
        post.setUser(user);

        CommentSaveRequest commentSaveRequest = new CommentSaveRequest(1L, "안녕하세요 댓글입니다.");

        Comment expectComment = commentConverter.converterToComment(commentSaveRequest, user, post);

        CommentResponse expectCommentResponse = new CommentResponse(1L,
                "안녕하세요 댓글입니다.",
                user.getNickname(),
                LocalDateTime.now(),
                user.getRegion().getNeighborhood(),
                null,
                0L);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(commentConverter.converterToComment(commentSaveRequest, user, post)).willReturn(expectComment);
        given(commentConverter.converterToCommentResponse(expectComment)).willReturn(expectCommentResponse);

        // WHEN
        CommentResponse commentResponse = commentService.save(commentSaveRequest, 1L);

        // THEN
        verify(userRepository, times(1)).findById(user.getId());
        verify(postRepository, times(1)).findById(post.getId());
        Comment comment = verify(commentConverter, atLeastOnce()).converterToComment(commentSaveRequest, user, post);
        verify(commentRepository, times(1)).save(comment);
        verify(commentConverter, times(1)).converterToCommentResponse(comment);

        assertThat(commentResponse.getId(), is(expectCommentResponse.getId()));
        assertThat(commentResponse.getContents(), is(expectCommentResponse.getContents()));
        assertThat(commentResponse.getParentId(), is(expectCommentResponse.getParentId()));
        assertThat(commentResponse.getDepth(), is(expectCommentResponse.getDepth()));
        assertThat(commentResponse.getRegion(), is(expectCommentResponse.getRegion()));
        assertThat(commentResponse.getNickName(), is(expectCommentResponse.getNickName()));
    }
}
