package com.kdt.localinfo.service;

import com.kdt.localinfo.controller.user.UserRequest;
import com.kdt.localinfo.controller.user.UserResponse;
import com.kdt.localinfo.model.User;
import com.kdt.localinfo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponse addUser(UserRequest userRequest) {
        User savedUser = userRepository.save(new User(userRequest));
        return new UserResponse(savedUser);
    }
}
