package com.kdt.localinfo.comment.repository;

import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT contents FROM Comment WHERE post_id = :id")
    List<Comment> findCommentsByPostId(@Param("id") Long postId);

    List<Comment> findAllByPost(Post post);
}
