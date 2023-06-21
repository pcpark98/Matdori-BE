package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
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

    public static StoreFavorite createStoreFavorite(User user, Store store){
        StoreFavorite storeFavorite = new StoreFavorite();
        storeFavorite.setStore(store);
        storeFavorite.setUser(user);
        return storeFavorite;
    }
}
