package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.Jokbo;
import com.matdori.matdori.domain.JokboComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

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
                        "WHERE c.jokbo.id = :id AND c.isDeleted = false " +
                        "ORDER BY c.id DESC", JokboComment.class)
                .setParameter("id", id)
                .setMaxResults(14)
                .getResultList();
    }

    /**
     * 내가 쓴 모든 댓글 조회하기.
     */
    public List<JokboComment> findByUserIndex(Long userId){
        return em.createQuery(
                        "SELECT c FROM JokboComment c " +
                                "JOIN FETCH c.jokbo j " +
                                "WHERE c.user.id = :userId AND c.isDeleted = false " +
                                "ORDER BY c.id DESC", JokboComment.class)
                .setParameter("userId", userId)
                .setMaxResults(14)
                .getResultList();
    }

    /**
     * 내가 쓴 댓글 조회 페이징 처리하여 조회하기.
     */
    public List<JokboComment> findCommentsDescendingById(Long userId, Long cursor) {
        return em.createQuery(
                        "SELECT c FROM JokboComment c " +
                                "JOIN FETCH c.jokbo j " +
                                "WHERE c.user.id = : userId AND  c.isDeleted = false AND c.id < :cursor " +
                                "ORDER BY c.id DESC", JokboComment.class)
                .setParameter("userId", userId)
                .setParameter("cursor" ,cursor)
                .setMaxResults(14)
                .getResultList();
    }

    /**
     * 특정 족보에 달린 댓글 조회 페이징 처리하여 조회하기.
     */
    public List<JokboComment> findCommentsAtJokboDescendingById(Long jokboId, Long cursor) {
        return em.createQuery(
                "SELECT c FROM  JokboComment c " +
                        "JOIN FETCH c.jokbo j " +
                        "WHERE c.isDeleted = false AND c.jokbo.id =: jokboId AND c.id < :cursor " +
                        "ORDER BY  c.id DESC", JokboComment.class)
                .setParameter("jokboId" ,jokboId)
                .setParameter("cursor", cursor)
                .setMaxResults(14)
                .getResultList();
    }

    /**
     * 족보 댓글 id로 족보 댓글 하나 조회하기.
     */
    public Optional<JokboComment> findOne (Long id) {
        JokboComment jokboComment = em.find(JokboComment.class, id);
        return Optional.ofNullable(jokboComment);
    }

    /**
     * 족보 댓글 삭제하기.
     */
    public void delete(Long id) {
        em.remove(em.find(JokboComment.class, id));
    }
}
