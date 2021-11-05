package com.kdt.localinfo.common;

import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.Role;
import com.kdt.localinfo.user.entity.User;

import java.util.Set;

public class TestEntityFactory {
    public static Comment.CommentBuilder commentBuilder() {
        User user = User.builder()
                .id(1L)
                .roles(Set.of(Role.GENERAL))
                .region(new Region("고양시", "덕양구", "행신동"))
                .name("test")
                .password("1234")
                .nickname("nickName")
                .email("test@gmail.com")
                .build();

        Post post = new Post(1L,"게시글", new Category(1L,"동네 맛집"), user.getRegion());
        post.setUser(user);

        return Comment.builder()
                .id(1L)
                .contents("댓글")
                .user(user)
                .post(post);
    }
}
