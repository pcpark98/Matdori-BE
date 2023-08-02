package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_index")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_index")
    private Store store;

    @NotBlank
    @Column(length = 30)
    @Size(max = 30)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Menu> menus = new ArrayList<>();

}
