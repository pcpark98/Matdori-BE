package com.matdori.matdori.domain;

import javax.persistence.*;

@Entity
public class StoreFavorite {

    @Id @GeneratedValue
    @Column(name = "store_favorite_index")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_index")
    private User user;

    @ManyToOne
    @JoinColumn(name = "store_index")
    private Store store;
}
