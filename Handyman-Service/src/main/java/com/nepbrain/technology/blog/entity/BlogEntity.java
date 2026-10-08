package com.nepbrain.technology.blog.entity;

import com.nepbrain.technology.core.entity.BaseAuditEntity;
import com.nepbrain.technology.core.enums.StatusEnum;
import lombok.*;

import jakarta.persistence.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hm_blog")
public class BlogEntity extends BaseAuditEntity {

    private String title;

    @Column(length = 8000)
    private String description;

    private String image;
    private Boolean isFeatured;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private String authorName;
}
