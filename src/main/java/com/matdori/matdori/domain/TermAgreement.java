package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    public TermAgreement(User user, TermsOfService terms) {
        this.user = user;
        this.termsOfService = terms;
    }
}
