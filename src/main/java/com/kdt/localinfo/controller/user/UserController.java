package com.kdt.localinfo.controller.user;

import com.kdt.localinfo.commons.ErrorResources;
import com.kdt.localinfo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users", produces = MediaTypes.HAL_JSON_VALUE)
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid UserRequest request, Errors errors) {
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
