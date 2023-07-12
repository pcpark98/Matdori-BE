package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.JokboFavorite;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JokboFavoriteRepository {

    private final EntityManager em;

    /**
     * 족보 좋아요 정보 저장하기.
     */
    public void save(JokboFavorite jokboFavorite) {
        em.persist(jokboFavorite);
    }

    /**
     * 족보 좋아요 정보 삭제하기.
     */
    public void delete(Long id) {
        em.remove(em.find(JokboFavorite.class, id));
    }

    /**
     * 내가 좋아요 누른 모든 족보 가져오기.
     */
    public List<JokboFavorite> findAllFavoriteJokbo(Long id, int pageCount) {
        return em.createQuery(
                "SELECT DISTINCT f FROM User u " +
                        "JOIN u.jokboFavorites f " +
                        "JOIN FETCH f.jokbo j " +
                        "LEFT JOIN FETCH j.jokboComments " +
                        "WHERE u.id =: id", JokboFavorite.class)
                .setParameter("id", id)
                .setFirstResult((pageCount-1)*15)
                .setMaxResults(15)
                .getResultList();
    }


}
