package com.kdt.localinfo.user.controller;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
public class IndexController {

    @GetMapping("/api/index")
    public RepresentationModel index() {
        var index = new RepresentationModel();
        index.add(linkTo(UserController.class).withRel("users"));
        return index;
    }
}
