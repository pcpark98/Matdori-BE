package com.matdori.matdori.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TermsOfService {

    @Id @GeneratedValue
    @Column(name = "term_index")
    private Long id;

    private String title;
    private String contents;
}
