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

    /**
     * 족보 저장하기.
     */
    public void save(Jokbo jokbo) {
        em.persist(jokbo);
    }

    /**
     * id로 단일 족보 검색하기.
     */
    public Jokbo findOne(Long id) {
        return em.find(Jokbo.class, id);
    }

    /**
     * 족보 삭제하기
     */
    public void delete(Long id) {
        em.remove(em.find(Jokbo.class, id));
    }

    /**
     * 모든 족보의 개수 구하기.
     */
    public int countAll() {
        return em.createQuery("SELECT j FROM Jokbo j", Jokbo.class)
                .getResultList().size();
    }







}
