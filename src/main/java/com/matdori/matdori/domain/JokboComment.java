package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class JokboComment {
    @Id @GeneratedValue
    @Column(name = "comment_index")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "jokbo_index")
    private Jokbo jokbo;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_index")
    private User user;

    private String contents;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "created_at")
    private String createdAt;
}
