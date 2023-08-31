package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"comment_index", "user_index"}
                )
        }
)
public class JokboCommentFavorite {

    @Id @GeneratedValue
    @Column(name = "comment_favorite_index")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "comment_index")
    private JokboComment jokboComment;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_index")
    private User user;
}
