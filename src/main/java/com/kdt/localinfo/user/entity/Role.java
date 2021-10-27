package com.kdt.localinfo.user.entity;

import com.kdt.localinfo.user.Status;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(generator = "ROLE_SEQ_ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status status;
}
