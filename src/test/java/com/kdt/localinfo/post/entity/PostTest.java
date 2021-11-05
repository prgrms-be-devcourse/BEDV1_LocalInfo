package com.kdt.localinfo.post.entity;

import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.Role;
import com.kdt.localinfo.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.junit.jupiter.api.Assertions.assertAll;

class PostTest {

    @Test
    @DisplayName("게시물 생성 성공 테스트")
    void createPost() {

        Region region = Region.builder()
                .neighborhood("neighbor")
                .district("district")
                .city("city")
                .build();

        User user = User.builder()
                .name("test")
                .nickname("testNickName")
                .email("test@mail.com")
                .password("testPassword")
                .roles(Set.of(Role.valueOf("GENERAL")))
                .region(region)
                .build();
        Category category = new Category(1L, "동네생활");

        Post post = Post.builder()
                .contents("this is contents")
                .user(user)
                .category(category)
                .photos(new ArrayList<>())
                .build();
        assertAll(
                () -> assertThat(post).isNotNull(),
                () -> assertThat(post.getContents()).isEqualTo("this is contents"),
                () -> assertThat(post.getUser()).isEqualTo(user),
                () -> assertThat(post.getCategory()).isEqualTo(category),
                () -> assertThat(post.getPhotos()).isNotNull()
        );

        Assertions.assertThat(post.getUser().getName()).isEqualTo(user.getName());
    }

    @Test
    @DisplayName("게시물 생성 실패 테스트")
    void notSave() {
        assertThatNullPointerException()
                .isThrownBy(() -> Post.builder()
                        .contents("")
                        .user(null)
                        .category(null)
                        .photos(null)
                        .build());
    }

}