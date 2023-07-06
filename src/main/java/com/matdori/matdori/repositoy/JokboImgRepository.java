package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.JokboImg;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class JokboImgRepository {

    private final EntityManager em;

    public void save(JokboImg jokboImg) {
        em.persist(jokboImg);
    }
}
