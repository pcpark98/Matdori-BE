package com.matdori.matdori.domain;

import javax.persistence.*;

@Embeddable
public class PopularMenu {

    @Id @GeneratedValue
    @Column(name = "popular_menu_index")
    private Long id;

    @OneToOne
    @JoinColumn(name = "menu_index")
    private Menu menu;

    @ManyToOne
    @JoinColumn(name = "store_index")
    private Store store;

}
