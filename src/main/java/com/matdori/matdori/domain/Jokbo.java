package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class Jokbo {
    @Id @GeneratedValue
    @Column(name = "jokbo_index")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_index")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "store_index")
    private Store store;

    private String title;
    private String contents;
    @Column(name = "total_rating")
    private Long totalRating;

    @Column(name = "flavor_rating")
    private Long flavorRating;

    @Column(name = "under_priced_rating")
    private Long underPricedRating;

    @Column(name = "clean_rating")
    private Long cleanRating;

    @Column(name = "created_at")
    private String createdAt;
}
