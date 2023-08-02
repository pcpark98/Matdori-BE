package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import java.time.LocalDateTime;

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

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String contents;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
