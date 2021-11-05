package com.kdt.localinfo.comment.controller;

import com.kdt.localinfo.comment.dto.CommentChangeRequest;
import com.kdt.localinfo.comment.dto.CommentResponse;
import com.kdt.localinfo.comment.dto.CommentSaveRequest;
import com.kdt.localinfo.comment.service.CommentService;
import com.kdt.localinfo.common.ErrorResources;
import com.kdt.localinfo.common.ValidationException;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping(produces = MediaTypes.HAL_JSON_VALUE)
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> ioExceptionHandler(IOException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> notFoundHandler(NotFoundException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<EntityModel<Errors>> badRequest(ValidationException ex) {
        return ResponseEntity.badRequest().body(ErrorResources.modelOf(ex.getErrors()));
    }

    @PostMapping(path = "/posts/{post-id}/comments", produces = MediaTypes.HAL_JSON_VALUE, consumes = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<CommentResponse>> save(
            @PathVariable("post-id") Long postId,
            @RequestParam(value = "images", required = false) List<MultipartFile> multipartFiles,
            @RequestBody @Validated CommentSaveRequest commentSaveRequest,
            Errors errors) throws NotFoundException, IOException {

        log.info("save execute");
        if (errors.hasErrors()) {
            throw new ValidationException("CommentSaveRequest Validation Error", errors);
        }

        CommentResponse commentResponse = commentService.save(commentSaveRequest, postId, multipartFiles);

        URI createdUri = linkTo(methodOn(CommentController.class).save(postId, multipartFiles, commentSaveRequest, errors)).toUri();

        EntityModel<CommentResponse> entityModel = EntityModel.of(commentResponse,
                linkTo(methodOn(CommentController.class).save(postId, multipartFiles, commentSaveRequest, errors)).withSelfRel(),
                linkTo(methodOn(CommentController.class).findAllByPostId(postId)).withRel("findAllByPostId"));

        return ResponseEntity.created(createdUri).body(entityModel);
    }

    @GetMapping(path = "/posts/{post-id}/comments", produces = MediaTypes.HAL_JSON_VALUE, consumes = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<CommentResponse>> findAllByPostId(@PathVariable("post-id") Long postId) throws NotFoundException {
        log.info("comment findAllByPostId execute");
        List<CommentResponse> commentResponses = commentService.findAllByPostId(postId);

        CollectionModel<CommentResponse> entityModel = CollectionModel.of(commentResponses,
                linkTo(methodOn(CommentController.class).findAllByPostId(postId)).withSelfRel());

        return ResponseEntity.ok().body(entityModel);
    }

    @PostMapping(path = "/posts/comments", produces = MediaTypes.HAL_JSON_VALUE, consumes = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<CommentResponse>> changeComment(
            @RequestParam(value = "images", required = false) List<MultipartFile> multipartFiles,
            @RequestBody @Validated CommentChangeRequest commentChangeRequest,
            Errors errors
    ) throws IOException {
        log.info("comment changeComment execute");
        CommentResponse commentResponse = commentService.changeComment(multipartFiles, commentChangeRequest);

        EntityModel<CommentResponse> entityModel = EntityModel.of(commentResponse,
                linkTo(methodOn(CommentController.class).changeComment(multipartFiles, commentChangeRequest, errors)).withSelfRel());

        return ResponseEntity.ok().body(entityModel);
    }
}
