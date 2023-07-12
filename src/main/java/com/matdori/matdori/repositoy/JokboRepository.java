package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.Jokbo;
import com.matdori.matdori.domain.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

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
    public Jokbo findOne(Long id) {
        return em.find(Jokbo.class, id);
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
    public List<Jokbo> findByUserIndex(Long userId){
        return em.createQuery(
                        "SELECT j FROM Jokbo j " +
                                "JOIN FETCH j.user u " +
                                "LEFT JOIN j.jokboImgs " +
                                "LEFT JOIN FETCH j.jokboComments c " +
                                "WHERE u.id =: userId", Jokbo.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Jokbo> findByStoreIndex(Long storeId, int startIndex){
        return em.createQuery(
                "SELECT j FROM Jokbo j " +
                        "WHERE j.store.id =: storeId", Jokbo.class)
                .setParameter("storeId", storeId)
                .setFirstResult(startIndex)
                .setMaxResults(15)
                .getResultList();
    }
}
