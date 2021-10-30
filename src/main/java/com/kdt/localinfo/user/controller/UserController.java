package com.kdt.localinfo.user.controller;

import com.kdt.localinfo.common.ErrorResources;
import com.kdt.localinfo.user.dto.UserRequest;
import com.kdt.localinfo.user.dto.UserResponse;
import com.kdt.localinfo.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
@RequestMapping(value = "/api/users", produces = MediaTypes.HAL_JSON_VALUE)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE, consumes = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity create(@RequestBody @Validated UserRequest request, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorResources.modelOf(errors));
        }
        UserResponse userResponse = userService.addUser(request);

        URI createdUri = linkTo(UserController.class).slash(userResponse).toUri();
        EntityModel<UserResponse> entityModel = EntityModel.of(userResponse,
                getLinkAddress().slash(userResponse.getId()).withSelfRel(),
                getLinkAddress().slash(userResponse.getId()).withRel("get"),
                getLinkAddress().slash(userResponse.getId()).withRel("delete"),
                getLinkAddress().slash(userResponse.getId()).withRel("edit"));
        return ResponseEntity.created(createdUri).body(entityModel);
    }

    private WebMvcLinkBuilder getLinkAddress() {
        return linkTo(UserController.class);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(ErrorResources.modelOf(errors));
    }

}
