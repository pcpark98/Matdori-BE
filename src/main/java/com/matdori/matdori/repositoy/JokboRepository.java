package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.Jokbo;
import com.matdori.matdori.domain.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JokboRepository {

    private final EntityManager em;

    public void save(Jokbo jokbo) {
        em.persist(jokbo);
    }


    public Jokbo findOne(Long id) {
        return em.find(Jokbo.class, id);
    }

    /**
     * 모든 족보의 개수 구하기.
     */
    public int countAll() {
        return em.createQuery("SELECT j FROM Jokbo j", Jokbo.class)
                .getResultList().size();
    }







}
