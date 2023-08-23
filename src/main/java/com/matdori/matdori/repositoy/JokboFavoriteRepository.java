package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.JokboFavorite;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JokboFavoriteRepository {

    private final EntityManager em;

    /**
     * 족보 좋아요 정보 저장하기.
     */
    public Long save(JokboFavorite jokboFavorite) {
        em.persist(jokboFavorite);
        return jokboFavorite.getId();
    }

    /**
     * 족보 좋아요 정보 삭제하기.
     */
    public void delete(List<Long> jokboIndices , Long userId){
        em.createQuery(
                        "DELETE FROM JokboFavorite f " +
                                "WHERE f.id IN :jokboIndices AND f.user.id = :userId"
                )
                .setParameter("jokboIndices", jokboIndices)
                .setParameter("userId", userId)
                .executeUpdate();
    }

    /**
     * 내가 좋아요 누른 모든 족보 가져오기.
     */
    public List<JokboFavorite> findAllFavoriteJokbo(Long userId) {
        return em.createQuery(
                "SELECT DISTINCT f FROM User u " +
                        "JOIN u.jokboFavorites f " +
                        "JOIN FETCH f.jokbo j " +
                        "LEFT JOIN FETCH j.jokboComments " +
                        "WHERE u.id =: id " +
                        "ORDER BY f.id DESC ", JokboFavorite.class)
                .setParameter("id", userId)
                .setMaxResults(14)
                .getResultList();
    }

    public List<JokboFavorite> getFavoriteStoresDescendingById(Long userId, Long favoriteJokboId) {
        return em.createQuery(
                        "SELECT DISTINCT f FROM User u " +
                                "JOIN u.jokboFavorites f " +
                                "JOIN FETCH f.jokbo j " +
                                "LEFT JOIN FETCH j.jokboComments " +
                                "WHERE u.id =: id AND f.id < :favoriteJokboId " +
                                "ORDER BY f.id DESC ", JokboFavorite.class)
                .setParameter("id", userId)
                .setParameter("favoriteJokboId", favoriteJokboId)
                .setMaxResults(14)
                .getResultList();
    }
    public JokboFavorite findOne(Long jokboFavoriteId) { return em.find(JokboFavorite.class, jokboFavoriteId);}


    public Optional<Long> readFavoriteStoreIndex(Long userId, Long storeId) {
        return em.createQuery(
                        "SELECT s.id FROM StoreFavorite s " +
                                "WHERE s.user.id =: userId AND s.store.id =: storeId", Long.class)
                .setParameter("userId", userId)
                .setParameter("storeId", storeId)
                .getResultList()
                .stream().findAny();
    }

    /**
     * 유저가 족보에 좋아요를 눌렀는지 여부 확인
     */
    public Optional<JokboFavorite> findByIds(Long userId, Long jokboId) {

        List<JokboFavorite> jokboFavorite = em.createQuery(
                "SELECT f FROM JokboFavorite f " +
                        "WHERE f.user.id =: userId " +
                        "AND f.jokbo.id =: jokboId ", JokboFavorite.class)
                .setParameter("userId", userId)
                .setParameter("jokboId", jokboId)
                .getResultList();

        return jokboFavorite.stream().findAny();
    }
}
