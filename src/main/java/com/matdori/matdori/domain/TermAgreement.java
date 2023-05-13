package com.matdori.matdori.domain;

import javax.persistence.*;

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
    private String createdAt;
}
