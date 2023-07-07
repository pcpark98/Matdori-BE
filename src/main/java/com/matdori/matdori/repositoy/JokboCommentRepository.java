package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.JokboComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

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

    /**
     * 족보에 매핑된 모든 댓글 조회하기.
     */
    public List<JokboComment> findAllJokboComments(Long id) {
        return em.createQuery(
                "SELECT c FROM JokboComment c "+
                        "WHERE c.jokbo.id = :id", JokboComment.class)
                .setParameter("id", id)
                .getResultList();
    }
}
