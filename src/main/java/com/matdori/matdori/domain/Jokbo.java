package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "flavor_rating")
    private int flavorRating;

    @Column(name = "under_priced_rating")
    private int underPricedRating;

    @Column(name = "clean_rating")
    private int cleanRating;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "jokbo")
    private List<JokboImg> jokboImgs = new ArrayList<>();

    @OneToMany(mappedBy = "jokbo")
    private List<JokboComment> jokboComments = new ArrayList<>();

    @OneToMany(mappedBy = "jokbo")
    private List<JokboFavorite> jokboFavorites = new ArrayList<>();
}
