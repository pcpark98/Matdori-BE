package com.matdori.matdori.domain;

import javax.persistence.*;

@Entity
public class Menu {

    @Id @GeneratedValue
    @Column(name = "menu_index")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_index")
    private Category category;

    private String name;
    private Integer price;
    private String img_url;
}