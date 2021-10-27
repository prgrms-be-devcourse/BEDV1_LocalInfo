package com.kdt.localinfo.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "regions")
public class Region {
    @Id
    @GeneratedValue(generator = "REGION_SEQ_ID")
    private Long id;
    private String name;
}
