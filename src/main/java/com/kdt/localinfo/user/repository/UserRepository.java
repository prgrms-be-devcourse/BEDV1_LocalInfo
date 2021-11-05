package com.kdt.localinfo.user.repository;

import com.kdt.localinfo.photo.Photo;
import com.kdt.localinfo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
}
