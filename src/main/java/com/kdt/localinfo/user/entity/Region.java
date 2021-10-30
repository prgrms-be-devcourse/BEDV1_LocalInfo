package com.kdt.localinfo.user.entity;

import lombok.*;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@EqualsAndHashCode
@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Access(AccessType.FIELD)
public class Region {

    @Column(nullable = false)
    private String neighborhood;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String city;
}
