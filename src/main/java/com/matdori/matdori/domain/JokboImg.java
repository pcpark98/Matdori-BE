package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class JokboImg {
    @Id @GeneratedValue
    @Column(name = "jokbo_img_index")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "jokbo_index")
    private Jokbo jokbo;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "created_at")
    private String createdAt;
}
