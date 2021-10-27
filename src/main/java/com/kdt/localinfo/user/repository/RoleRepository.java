package com.kdt.localinfo.user.repository;

import com.kdt.localinfo.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
