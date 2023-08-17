package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.Jokbo;
import com.matdori.matdori.domain.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

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
    public Optional<Jokbo> findOne(Long id) {
        Jokbo jokbo = em.find(Jokbo.class, id);
        return Optional.ofNullable(jokbo);
    }

    /**
     * 족보 삭제하기.
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


    // FETCH JOIN
    // ToOne은 여러 개 사용 가능하지만
    // ToMany는 여러 개 사용 불가능
    // @OneToMany, @ManyToMany와 같이 N 관계의 자식 엔티티에 관해서는 가장 데이터가 많은 자식쪽에 Fetch Join을 사용한다.
    // https://jojoldu.tistory.com/457

    /**
     * 내가 쓴 모든 족보 조회하기.
     */
    public List<Jokbo> findByUserIndex(Long userId){
        return em.createQuery(
                        "SELECT j FROM Jokbo j " +
                                "JOIN FETCH j.user u " +
                                "LEFT JOIN j.jokboImgs " +
                                "LEFT JOIN FETCH j.jokboComments c " +
                                "WHERE u.id =: userId " +
                                "ORDER BY j.id DESC", Jokbo.class)
                .setParameter("userId", userId)
                .setMaxResults(14)
                .getResultList();
    }

    public List<Jokbo> getJokboDescendingById(Long userId, Long cursor){
        return em.createQuery(
                        "SELECT j FROM Jokbo j " +
                                "JOIN FETCH j.user u " +
                                "LEFT JOIN j.jokboImgs " +
                                "LEFT JOIN FETCH j.jokboComments c " +
                                "WHERE u.id =: userId AND j.id < : cursor " +
                                "ORDER BY j.id DESC", Jokbo.class)
                .setParameter("userId", userId)
                .setParameter("cursor", cursor)
                .setMaxResults(14)
                .getResultList();
    }

    public List<Jokbo> findByStoreIndex(Long storeId){
        return em.createQuery(
                "SELECT j FROM Jokbo j " +
                        "WHERE j.store.id =: storeId " +
                        "ORDER BY j.id DESC ", Jokbo.class)
                .setParameter("storeId", storeId)
                .setMaxResults(14)
                .getResultList();
    }
    public List<Jokbo> findJokbosDescendingById(Long storeId, Long cursor){
        return em.createQuery(
                        "SELECT j FROM Jokbo j " +
                                "WHERE j.store.id =: storeId AND j.id < :cursor " +
                                "ORDER BY j.id DESC ", Jokbo.class)
                .setParameter("storeId", storeId)
                .setParameter("cursor", cursor)
                .setMaxResults(14)
                .getResultList();
    }

    /**
     * 가게에 매핑된 모든 족보의 개수 구하기.
     */
    public int countAllAtStore(Long storeId) {
        return em.createQuery("SELECT j FROM Jokbo j " +
                        "WHERE j.store.id =: storeId", Jokbo.class)
                .setParameter("storeId", storeId)
                .getResultList().size();
    }

    /**
     * 가게에서 가장 인기 있는 족보
     */
    public Optional<Jokbo> readPopularJokboatStore(Long storeId){
        return em.createQuery(
                "SELECT j FROM Jokbo j " +
                "WHERE j.store.id =: storeId " +
                "ORDER BY j.jokboFavorites.size DESC ", Jokbo.class)
                .setMaxResults(1)
                .setParameter("storeId", storeId)
                .getResultList()
                .stream().findAny();
    }
}
