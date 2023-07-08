package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.Jokbo;
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

    /**
     * 내가 쓴 모든 댓글 조회하기.
     */
    public List<JokboComment> findByUserIndex(Long userId){
        return em.createQuery(
                        "SELECT c FROM JokboComment c " +
                                "JOIN c.user u ON u.id =: userId AND u.id = c.user.id " +
                                "JOIN FETCH c.jokbo j " +
                                "JOIN FETCH j.store " +
                                "WHERE c.isDeleted = false ", JokboComment.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    /**
     * 족보 댓글 id로 족보 댓글 하나 조회하기.
     */
    public JokboComment findOne (Long id) {
        return em.find(JokboComment.class, id);
    }

    /**
     * 족보 댓글 삭제하기.
     */
    public void delete(Long id) {
        em.remove(em.find(JokboComment.class, id));
    }
}
