package com.kdt.localinfo.post.repository;

import com.kdt.localinfo.post.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
