package com.kdt.localinfo.post.controller;

import com.kdt.localinfo.post.dto.PostCreateRequest;
import com.kdt.localinfo.post.dto.PostResponse;
import com.kdt.localinfo.post.dto.PostUpdateRequest;
import com.kdt.localinfo.post.service.PostService;
import javassist.NotFoundException;
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

    @PostMapping(value = "/posts", consumes = {"multipart/form-data"})
    public ResponseEntity<Void> write(@ModelAttribute PostCreateRequest request) throws IOException, NotFoundException {
        Long postId = postService.savePost(request);

        return ResponseEntity.created(URI.create("/api/posts/" + postId)).build();
    }

    @GetMapping("posts/{post-id}")
    public ResponseEntity<PostResponse> findDetailPost(@PathVariable(name = "post-id") Long postId) throws NotFoundException {
        return ResponseEntity.ok(postService.findDetailPost(postId));
    }

    @GetMapping("posts/categories/{category-id}")
    public ResponseEntity<List<PostResponse>> findPostsByCategory(@PathVariable(name = "category-id") Long categoryId) {
        List<PostResponse> posts = postService.findAllByCategory(categoryId);
        return ResponseEntity.ok(posts);
    }

    @PutMapping(value = "/posts/{post-id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Void> updatePost(@ModelAttribute PostUpdateRequest request) throws NotFoundException, IOException {
        postService.updatePost(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{post-id}")
    public ResponseEntity<Long> deletePost(@PathVariable(name = "post-id") Long postId) {
        return ResponseEntity.ok(postService.deletePost(postId));
    }
}
