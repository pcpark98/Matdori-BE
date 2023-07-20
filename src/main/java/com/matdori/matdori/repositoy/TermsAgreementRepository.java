package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.TermAgreement;
import com.matdori.matdori.domain.TermsOfService;
import com.matdori.matdori.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class TermsAgreementRepository {
    private final EntityManager em;
    public void save(TermAgreement term) { em.persist(term);}

}
