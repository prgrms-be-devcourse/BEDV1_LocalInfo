package com.kdt.localinfo.post.controller;

import com.kdt.localinfo.post.dto.PostCreateRequest;
import com.kdt.localinfo.post.dto.PostResponse;
import com.kdt.localinfo.post.dto.PostUpdateRequest;
import com.kdt.localinfo.post.service.PostService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;

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

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> findDetailPost(@PathVariable Long postId) throws NotFoundException {
        return ResponseEntity.ok(postService.findDetailPost(postId));
    }

    @GetMapping("posts/categories/{categoryId}")
    public ResponseEntity<List<PostResponse>> findPostsByCategory(@PathVariable Long categoryId) {
        List<PostResponse> posts = postService.findAllByCategory(categoryId);
        return ResponseEntity.ok(posts);
    }

    @PutMapping(value = "/posts/{postId}", consumes = {"multipart/form-data"})
    public ResponseEntity<Long> updatePost(
            @PathVariable Long postId, @ModelAttribute PostUpdateRequest request
    ) throws NotFoundException, IOException {
        return ResponseEntity.ok(postService.updatePost(postId, request));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Long> deletePost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.deletePost(postId));
    }
}
