package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter @Setter
public class Menu {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_index")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_index")
    private Category category;


    @NotBlank
    @Column(length = 30)
    @Size(max =30)
    private String name;

    @NotBlank
    @Column(length = 20)
    @Size(max =20)
    private String price;


    @Column(length = 255)
    private String imgUrl;
}