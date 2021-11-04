package com.kdt.localinfo.post.controller;

import com.kdt.localinfo.post.dto.PostCreateRequest;
import com.kdt.localinfo.post.dto.PostResponse;
import com.kdt.localinfo.post.dto.PostUpdateRequest;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.post.service.PostService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void create(@RequestPart("file") List<MultipartFile> multipartFiles,
                       @RequestPart("request") PostCreateRequest request,
                       HttpServletResponse response) throws NotFoundException, IOException {

        if (multipartFiles.size() == 0) {
            multipartFiles = new ArrayList<>();
        }
        Post post = postService.createPost(multipartFiles, request);
        postService.savePost(post);

        response.setStatus(HttpStatus.CREATED.value());
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
    public void updatePost(@PathVariable(name = "post-id") Long postId, @RequestBody PostUpdateRequest request,
                           HttpServletResponse response) throws NotFoundException, IOException {
        postService.updatePost(postId, request);
        response.setStatus(HttpStatus.OK.value());
    }

    @DeleteMapping("/posts/{post-id}")
    public ResponseEntity<Long> deletePost(@PathVariable(name = "post-id") Long postId) {
        return ResponseEntity.ok(postService.deletePost(postId));
    }
}
