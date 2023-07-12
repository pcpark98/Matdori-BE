package com.matdori.matdori.repositoy;

import com.matdori.matdori.controller.StoreApiController;
import com.matdori.matdori.domain.Category;
import com.matdori.matdori.domain.Jokbo;
import com.matdori.matdori.domain.Menu;
import com.matdori.matdori.domain.Store;
import com.matdori.matdori.repositoy.Dto.StoreListByDepartment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StoreRepository {

    private final EntityManager em;
    public Store findOne(Long id) { return em.find(Store.class, id);}

    public List<Store> findAll(){
        return em.createQuery("SELECT s FROM Store s", Store.class)
                .getResultList();
    }

    public List<Category> findAllCategoryWithMenu(Long id){
        return em.createQuery(
                "SELECT c FROM Category c " +
                        "JOIN FETCH c.store s " +
                        "WHERE s.id =: id", Category.class
        ).setParameter("id", id).getResultList();
    }

    /**
     * 해당 학과의 족보가 가장 많은 가게 리스트 구하기.
     */
    public List<StoreListByDepartment> getStoreListByDepartment(String department) {
        return em.createQuery(
                "SELECT new com.matdori.matdori.repositoy.Dto.StoreListByDepartment(s.id, s.name, s.imgUrl) " +
                        "FROM Jokbo j " +
                        "JOIN j.store s " +
                        "JOIN j.user u " +
                        "GROUP BY s.id, s.name, s.imgUrl, u.department " +
                        "HAVING u.department =: department " +
                        "ORDER BY s.jokbos.size DESC ", StoreListByDepartment.class)
                .setParameter("department", department)
                .setMaxResults(10)
                .getResultList();
    }

    /**
     * 해당 가게의 별점 평균 구하기
     */
    public Double getTotalRating(Store store) {
        return em.createQuery(
                "SELECT AVG((j.flavorRating + j.underPricedRating + j.cleanRating)/3) " +
                        "FROM Jokbo j " +
                        "WHERE j.store =: store ", Double.class)
                .setParameter("store", store)
                .getSingleResult();
    }

    public com.matdori.matdori.repositoy.Dto.StoreInformationHeader readStoreInformationHeader(Long storeId){
        return em.createQuery(
                        "SELECT new com.matdori.matdori.repositoy.Dto.StoreInformationHeader(j.store.name, " +
                                "AVG(j.flavorRating), AVG(j.cleanRating),AVG(j.underPricedRating), j.store.imgUrl ) " +
                                "FROM Jokbo j " +
                                "JOIN j.store " +
                                "GROUP BY j.store.name, j.store.id, j.store.imgUrl " +
                                "HAVING j.store.id =:storeId", com.matdori.matdori.repositoy.Dto.StoreInformationHeader.class)
                .setParameter("storeId", storeId)
                .getSingleResult();
    }
}
