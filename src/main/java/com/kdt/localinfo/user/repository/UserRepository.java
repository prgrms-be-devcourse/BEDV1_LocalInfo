package com.kdt.localinfo.user.repository;

import com.kdt.localinfo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
