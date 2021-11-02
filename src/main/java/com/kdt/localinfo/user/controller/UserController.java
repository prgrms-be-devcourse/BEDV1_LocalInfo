package com.kdt.localinfo.user.controller;

import com.kdt.localinfo.common.ErrorResources;
import com.kdt.localinfo.common.ValidationException;
import com.kdt.localinfo.user.dto.UserRequest;
import com.kdt.localinfo.user.dto.UserResponse;
import com.kdt.localinfo.user.service.UserService;
import javassist.NotFoundException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/users", produces = MediaTypes.HAL_JSON_VALUE)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> notFoundHandler(NotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<EntityModel<Errors>> badRequest(ValidationException ex) {
        return ResponseEntity.badRequest().body(ErrorResources.modelOf(ex.getErrors()));
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE, consumes = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<UserResponse>> create(@RequestBody @Validated UserRequest request, Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException("UserRequest has invalid input error", errors);
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

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<UserResponse>>> listUsers() throws Exception {
        List<UserResponse> userList = userService.getUserList();
        List<EntityModel<UserResponse>> collect = userList.stream()
                .map(userResponse -> EntityModel.of(userResponse,
                        getLinkAddress().slash(userResponse.getId()).withRel("get"),
                        getLinkAddress().slash(userResponse.getId()).withRel("delete")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(collect, getLinkAddress().withSelfRel()));
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE, value = "/{id}")
    public ResponseEntity<EntityModel<UserResponse>> getUser(@PathVariable Long id) throws Exception {
        UserResponse userResponse = userService.getUser(id);

        EntityModel<UserResponse> entityModel = EntityModel.of(userResponse,
                getLinkAddress().slash(userResponse.getId()).withSelfRel(),
                getLinkAddress().slash(userResponse.getId()).withRel("delete"),
                getLinkAddress().slash(userResponse.getId()).withRel("edit")
        );
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).listUsers());
        entityModel.add(linkTo.withRel("user-list"));
        return ResponseEntity.ok(entityModel);
    }


    private WebMvcLinkBuilder getLinkAddress() {
        return linkTo(UserController.class);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(ErrorResources.modelOf(errors));
    }

}
