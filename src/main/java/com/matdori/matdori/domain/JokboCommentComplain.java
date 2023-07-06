package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class JokboCommentComplain {
    @Id @GeneratedValue
    @Column(name = "comment_complain_index")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "comment_index")
    private JokboComment jokboComment;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_index")
    private User user;

    private String contents;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
