package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    @NotBlank
    @Column(length = 50)
    @Size(max = 50)
    private String title;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String contents;

    @NotNull
    @Column(name = "flavor_rating")
    private int flavorRating;

    @NotNull
    @Column(name = "under_priced_rating")
    private int underPricedRating;

    @NotNull
    @Column(name = "clean_rating")
    private int cleanRating;

    @NotNull
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "jokbo", cascade = CascadeType.ALL)
    private List<JokboImg> jokboImgs = new ArrayList<>();

    @OneToMany(mappedBy = "jokbo", cascade = CascadeType.ALL)
    private List<JokboComment> jokboComments = new ArrayList<>();

    @OneToMany(mappedBy = "jokbo", cascade = CascadeType.ALL)
    private List<JokboFavorite> jokboFavorites = new ArrayList<>();
}
