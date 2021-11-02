package com.kdt.localinfo.user.service;

import com.kdt.localinfo.user.dto.UserRequest;
import com.kdt.localinfo.user.dto.UserResponse;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse addUser(UserRequest userRequest) {
        User savedUser = userRepository.save(userRequest.toEntity());
        return new UserResponse(savedUser);
    }

    public List<UserResponse> getUserList() throws Exception {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    public UserResponse getUser(Long id) throws NotFoundException {
        return userRepository.findById(id).map(UserResponse::new)
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));
    }
}
