package com.kdt.localinfo.post.controller;

import com.kdt.localinfo.common.ApiResponse;
import com.kdt.localinfo.post.dto.PostDto;
import com.kdt.localinfo.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController {

    @Autowired
    private PostService postService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/posts")
    public ApiResponse<Long> write(@RequestBody PostDto postDto) {
        Long savedPostId = postService.createPost(postDto);
        return ApiResponse.successCreated(savedPostId);
    }
}
