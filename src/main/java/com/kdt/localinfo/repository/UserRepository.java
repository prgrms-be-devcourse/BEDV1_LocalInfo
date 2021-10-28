package com.kdt.localinfo.repository;

import com.kdt.localinfo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
