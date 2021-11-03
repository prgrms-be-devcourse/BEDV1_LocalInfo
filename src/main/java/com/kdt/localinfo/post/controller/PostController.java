package com.kdt.localinfo.post.controller;

import com.kdt.localinfo.post.dto.PostCreateRequest;
import com.kdt.localinfo.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/posts", consumes = {"multipart/form-data"})
    public ResponseEntity<Void> write(@ModelAttribute PostCreateRequest request) throws IOException {
        Long postId = postService.createPost(request);
        return ResponseEntity.created(URI.create("/posts/" + postId)).build();
    }
}
