package com.kdt.localinfo.post.repository;

import com.kdt.localinfo.photo.Photo;
import com.kdt.localinfo.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "SELECT url FROM Photo WHERE post_id = :id")
    List<Photo> findPhotoByPostId(@Param("id") Long postId);

    List<Post> findPostByCategoryId(Long categoryId);
}
