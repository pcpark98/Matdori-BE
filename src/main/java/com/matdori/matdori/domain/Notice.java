package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Notice {
    @Id @GeneratedValue
    @Column(name = "notice_index")
    private Long id;
    private String title;
    private String contents;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "created_at")
    private String createdAt;
}
