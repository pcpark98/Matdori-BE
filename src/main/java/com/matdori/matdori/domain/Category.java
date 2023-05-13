package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class Category {
    @Id @GeneratedValue
    @Column(name = "category_index")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_index")
    private Store store;

    private String name;

}
