package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.JokboImg;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

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

    /**
     * id로 족보 이미지 조회하기
     */
    public Optional<JokboImg> findOne(Long id) {
        JokboImg jokboImg = em.find(JokboImg.class, id);
        return Optional.ofNullable(jokboImg);
    }
}
