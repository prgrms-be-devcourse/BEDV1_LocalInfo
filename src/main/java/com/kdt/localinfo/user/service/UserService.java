package com.kdt.localinfo.user.service;

import com.kdt.localinfo.user.dto.UserRequest;
import com.kdt.localinfo.user.dto.UserResponse;
import com.kdt.localinfo.user.entity.Region;
import com.kdt.localinfo.user.entity.Role;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public UserResponse addUser(UserRequest userRequest) {
        User savedUser = userRepository.save(userRequest.toEntity());
        return new UserResponse(savedUser);
    }

    public List<UserResponse> getUserList() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter(user -> user.getDeletedAt() == null)
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    public UserResponse getUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty() || user.get().getDeletedAt() != null) {
            throw new EntityNotFoundException("해당 유저가 존재하지 않습니다.");
        }
        return new UserResponse(user.get());
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User beforeUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));
        modelMapper.map(userRequest, beforeUser);
        beforeUser.getRoles().add(Role.valueOf(userRequest.getRole()));
        beforeUser.setRegion(Region.builder()
                .district(userRequest.getDistrict())
                .neighborhood(userRequest.getDistrict())
                .city(userRequest.getCity())
                .build());
        return new UserResponse(userRepository.save(beforeUser));
    }
}
