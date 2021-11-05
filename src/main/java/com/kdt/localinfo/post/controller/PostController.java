package com.kdt.localinfo.post.controller;

import com.kdt.localinfo.post.dto.PostCreateRequest;
import com.kdt.localinfo.post.dto.PostResponse;
import com.kdt.localinfo.post.dto.PostUpdateRequest;
import com.kdt.localinfo.post.service.PostService;
import javassist.NotFoundException;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(produces = MediaTypes.HAL_JSON_VALUE)
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(value = "/posts", produces = MediaTypes.HAL_JSON_VALUE, consumes = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<PostResponse>> write(
            @RequestParam(value = "images", required = false) List<MultipartFile> multipartFiles,
            @RequestBody @Validated PostCreateRequest request,
            Errors errors) throws IOException, NotFoundException {
        if  (errors.hasErrors()) {
            throw new ValidationException("PostCreateRequest Invalid Input", errors);
        }
        PostResponse postResponse = postService.savePost(request, multipartFiles);
        URI createdUri = linkTo(methodOn(PostController.class).write(multipartFiles, request, errors)).toUri();

        EntityModel<PostResponse> entityModel = EntityModel.of(postResponse,
                linkTo(methodOn(PostController.class).write(multipartFiles, request, errors)).withSelfRel(),
                linkTo(methodOn(PostController.class).findPostsByCategory(request.getCategoryId())).withRel("all posts"));
        return ResponseEntity.created(createdUri).body(entityModel);
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

    @PutMapping(value = "posts/{post-id}")
    public ResponseEntity<Void> updatePost(
            @PathVariable(name = "post-id") Long postId,
            @ModelAttribute PostUpdateRequest request) throws NotFoundException, IOException {
        postService.updatePost(postId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{post-id}")
    public ResponseEntity<Long> deletePost(@PathVariable(name = "post-id") Long postId) {
        return ResponseEntity.ok(postService.deletePost(postId));
    }
}
