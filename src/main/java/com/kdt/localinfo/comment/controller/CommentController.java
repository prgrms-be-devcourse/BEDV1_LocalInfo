package com.kdt.localinfo.comment.controller;

import com.kdt.localinfo.comment.dto.CommentResponse;
import com.kdt.localinfo.comment.dto.CommentSaveRequest;
import com.kdt.localinfo.comment.service.CommentService;
import com.kdt.localinfo.error.ErrorResources;
import com.kdt.localinfo.error.ValidationException;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> notFoundHandler(NotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<EntityModel<Errors>> badRequest(ValidationException ex) {
        return ResponseEntity.badRequest().body(ErrorResources.modelOf(ex.getErrors()));
    }

    @PostMapping(path = "/posts/{post-id}/comments", produces = MediaTypes.HAL_JSON_VALUE, consumes = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<CommentResponse>> save(@PathVariable("post-id") Long postId, @RequestBody @Validated CommentSaveRequest commentSaveRequest, Errors errors) throws NotFoundException, NoSuchMethodException {
        log.info("save execute");
        if (errors.hasErrors()) {
            throw new ValidationException("CommentSaveRequest Validation Error", errors);
        }

        CommentResponse commentResponse = commentService.save(commentSaveRequest, postId);
        URI createdUri = linkTo(methodOn(CommentController.class).save(postId, commentSaveRequest, errors)).toUri();
        EntityModel<CommentResponse> entityModel = EntityModel.of(commentResponse,
                linkTo(methodOn(CommentController.class).save(postId, commentSaveRequest, errors)).withSelfRel());

        return ResponseEntity.created(createdUri).body(entityModel);
    }
}
