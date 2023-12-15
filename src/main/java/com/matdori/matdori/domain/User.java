package com.matdori.matdori.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Range;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id @GeneratedValue
    @Column(name = "user_index")
    private Long id;

    @NotBlank
    @Column(length = 40)
    @Size(max = 40)
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    @Column(length = 30,unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Department department;

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
