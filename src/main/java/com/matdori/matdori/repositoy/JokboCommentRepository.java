package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.JokboComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class JokboCommentRepository {

    private final EntityManager em;

    /**
     * 족보 댓글 등록하기.
     */
    public void save(JokboComment jokboComment) {
        em.persist(jokboComment);
    }
}
