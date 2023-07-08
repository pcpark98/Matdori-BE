package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "users")
public class User {

    @Id @GeneratedValue
    @Column(name = "user_index")
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String department;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    private List<StoreFavorite> storeFavorites;

    @OneToMany(mappedBy = "user")
    private List<Jokbo> jokbos;

    @OneToMany(mappedBy = "user")
    private List<JokboComment> jokboComments;

    @OneToMany(mappedBy = "user")
    private List<JokboFavorite> jokboFavorites;
}
