package com.kdt.localinfo.post.repository;

import com.kdt.localinfo.post.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
