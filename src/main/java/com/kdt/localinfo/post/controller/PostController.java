package com.kdt.localinfo.post.controller;

import com.kdt.localinfo.common.ApiResponse;
import com.kdt.localinfo.post.dto.PostDto;
import com.kdt.localinfo.post.service.PostService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/posts")
    public ApiResponse<Long> write(@RequestBody PostDto postDto) {
        Long savedPostId = postService.createPost(postDto);
        return ApiResponse.successCreated(savedPostId);
    }

    @GetMapping("/posts/{id}")
    public ApiResponse<PostDto> findDetailPost(@PathVariable Long id) throws NotFoundException {
        PostDto foundPost = postService.findDetailPost(id);
        return ApiResponse.ok(foundPost);
    }

    @GetMapping("/categories/{categoryId}")
    public ApiResponse<List<PostDto>> findPostsByCategory(@PathVariable Long categoryId) throws NotFoundException {
        List<PostDto> posts = postService.findAllByCategory(categoryId);
        return ApiResponse.ok(posts);
    }

}
