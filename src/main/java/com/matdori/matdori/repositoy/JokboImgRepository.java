package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.JokboImg;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class JokboImgRepository {

    private final EntityManager em;

    /**
     * 족보 이미지 저장하기.
     */
    public void save(JokboImg jokboImg) {
        em.persist(jokboImg);
    }

    /**
     * 족보 이미지 삭제하기.
     */
    public void delete(Long id) {
        em.remove(em.find(JokboImg.class, id));
    }
}
