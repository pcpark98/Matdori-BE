package com.matdori.matdori.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TermAgreement {

    @Id @GeneratedValue
    @Column(name = "term_agreement_index")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_index")
    private User user;

    @ManyToOne
    @JoinColumn(name = "term_index")
    private TermsOfService termsOfService;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
