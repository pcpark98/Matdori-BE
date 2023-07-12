package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.TermsOfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TermsOfServiceRepository {
    private final EntityManager em;
    public List<TermsOfService> findAllTerms(){ return em.createQuery("SELECT t FROM TermsOfService t", TermsOfService.class).getResultList(); }

}
