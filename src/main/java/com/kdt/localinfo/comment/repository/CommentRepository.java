package com.kdt.localinfo.comment.repository;

import com.kdt.localinfo.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
