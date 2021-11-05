package com.kdt.localinfo.post.controller;

import com.kdt.localinfo.error.InvalidInputException;
import com.kdt.localinfo.post.dto.PostCreateRequest;
import com.kdt.localinfo.post.dto.PostResponse;
import com.kdt.localinfo.post.dto.PostUpdateRequest;
import com.kdt.localinfo.post.service.PostService;
import javassist.NotFoundException;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ValidationException;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(produces = MediaTypes.HAL_JSON_VALUE, value = "/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE, consumes = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<PostResponse>> write(
            @RequestParam(value = "images", required = false) List<MultipartFile> multipartFiles,
            @RequestBody @Validated PostCreateRequest request,
            Errors errors) throws IOException, NotFoundException {
        if (errors.hasErrors()) {
            throw new InvalidInputException("PostCreateRequest Invalid Input", errors);
        }
        PostResponse postResponse = postService.savePost(request, multipartFiles);
        URI createdUri = linkTo(methodOn(PostController.class).write(multipartFiles, request, errors)).toUri();

        EntityModel<PostResponse> entityModel = EntityModel.of(postResponse,
                linkTo(methodOn(PostController.class).write(multipartFiles, request, errors)).withSelfRel(),
                linkTo(methodOn(PostController.class).findPostsByCategory(request.getCategoryId())).withRel("all posts"));
        return ResponseEntity.created(createdUri).body(entityModel);
    }

    @GetMapping(value = "/{post-id}")
    public ResponseEntity<PostResponse> findDetailPost(@PathVariable(name = "post-id") Long postId) throws NotFoundException {
        return ResponseEntity.ok(postService.findDetailPost(postId));
    }

    @GetMapping(value = "/categories/{category-id}")
    public ResponseEntity<List<PostResponse>> findPostsByCategory(@PathVariable(name = "category-id") Long categoryId) {
        List<PostResponse> posts = postService.findAllByCategory(categoryId);
        return ResponseEntity.ok(posts);
    }

    @PostMapping(value = "/{postId}", produces = MediaTypes.HAL_JSON_VALUE, consumes = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<PostResponse>> updatePost(
            @PathVariable Long postId,
            @RequestParam(value = "images", required = false) List<MultipartFile> multipartFiles,
            @RequestBody @Validated PostUpdateRequest request,
            Errors errors) throws NotFoundException, IOException {

        if (errors.hasErrors()) {
            throw new InvalidInputException("PostUpdateRequest Invalid Input", errors);
        }

        PostResponse postResponse = postService.updatePost(postId, request, multipartFiles);
        URI updatedUri = linkTo(methodOn(PostController.class)
                .updatePost(postId, multipartFiles, request, errors))
                .toUri();

        EntityModel<PostResponse> entityModel = EntityModel.of(postResponse,
                linkTo(methodOn(PostController.class).updatePost(postId, multipartFiles, request, errors)).withSelfRel(),
                linkTo(methodOn(PostController.class).findPostsByCategory(request.getCategoryId())).withRel("all posts"));
        return ResponseEntity.ok().body(entityModel);
    }

    @DeleteMapping(value = "/{post-id}")
    public ResponseEntity<Long> deletePost(@PathVariable(name = "post-id") Long postId) {
        return ResponseEntity.ok(postService.deletePost(postId));
    }
}
