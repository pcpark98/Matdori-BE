package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

    @NotBlank
    @Column(length = 25)
    @Size(max = 25)
    private String email;

    @NotBlank
    @Column(length = 255)
    private String password;

    @NotBlank
    @Column(length = 30,unique = true)
    private String nickname;

    @NotBlank
    @Column(length = 20)
    @Size(max =20)
    private String department;

    @CreationTimestamp
    @Column(name = "created_at")
    @NotNull
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
