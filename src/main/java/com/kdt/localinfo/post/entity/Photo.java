package com.kdt.localinfo.post.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue(generator = "PHOTO_SEQ_ID")
    private Long id;
    private String url;
}
