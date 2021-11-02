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

    @GetMapping("/posts/{postId}")
    public ApiResponse<PostDto> findDetailPost(@PathVariable Long postId) throws NotFoundException {
        PostDto foundPost = postService.findDetailPost(postId);
        return ApiResponse.ok(foundPost);
    }

    @GetMapping("posts/categories/{categoryId}")
    public ApiResponse<List<PostDto>> findPostsByCategory(@PathVariable Long categoryId) {
        List<PostDto> posts = postService.findAllByCategory(categoryId);
        return ApiResponse.ok(posts);
    }

    @PutMapping("/posts/{postId}")
    public ApiResponse<Long> updatePost(@PathVariable Long postId, @RequestBody PostDto postDto) throws NotFoundException {
        return ApiResponse.ok(postService.updatePost(postId, postDto));
    }

    @DeleteMapping("/posts/{postId}")
    public ApiResponse<Long> deletePost(@PathVariable Long postId) {
        return ApiResponse.ok(postService.deletePost(postId));
    }

}
