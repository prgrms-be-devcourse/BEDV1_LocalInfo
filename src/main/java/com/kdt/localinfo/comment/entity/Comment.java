package com.kdt.localinfo.comment.entity;

import com.kdt.localinfo.user.entity.User;

import javax.persistence.*;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Comment() {}
}
