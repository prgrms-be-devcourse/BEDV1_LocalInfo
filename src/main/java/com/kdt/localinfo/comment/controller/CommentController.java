package com.kdt.localinfo.comment.controller;

import com.kdt.localinfo.comment.dto.CommentResponse;
import com.kdt.localinfo.comment.dto.CommentSaveRequest;
import com.kdt.localinfo.comment.service.CommentService;
import com.kdt.localinfo.common.ApiResponse;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ApiResponse<String> notFoundHandler(NotFoundException e) {
        return ApiResponse.fail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/posts/{post-id}/comments")
    public ApiResponse<CommentResponse> save(@RequestBody CommentSaveRequest commentSaveRequest, @PathVariable("post-id") Long postId) throws NotFoundException {
        return ApiResponse.successCreated(commentService.save(commentSaveRequest, postId));
    }

}
