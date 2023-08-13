package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.Category;
import com.matdori.matdori.domain.Department;
import com.matdori.matdori.domain.Store;
import com.matdori.matdori.domain.StoreCategory;
import com.matdori.matdori.repositoy.Dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public List<StoreListByDepartment> getStoreListByDepartment(Department department) {
        // 해당 학과의 족보가 달린 가게가 3개 이하일 경우에 대한 처리 필요.
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

    public Optional<com.matdori.matdori.repositoy.Dto.StoreInformationHeader> readStoreInformationHeader(Long storeId){
        return em.createQuery(
                        "SELECT new com.matdori.matdori.repositoy.Dto.StoreInformationHeader(s.name, " +
                                "AVG(j.flavorRating) , AVG(j.cleanRating) ,AVG(j.underPricedRating), s.imgUrl) " +
                                "FROM Store s " +
                                "LEFT JOIN s.jokbos j " +
                                "GROUP BY s.name, s.id, s.imgUrl " +
                                "HAVING s.id =:storeId", com.matdori.matdori.repositoy.Dto.StoreInformationHeader.class)
                .setParameter("storeId", storeId)
                .getResultList().stream().findAny();
    }

    /**
     * 맛도리 픽 가게 리스트 조회하기.
     */
    public List<MatdoriPick> getMatdoriPick(String department) {
        // 없는 학과에 대한 예외처리 필요.

        String sql = "(SELECT store_index, name, img_url " +
                "FROM store " +
                "ORDER BY RANDOM()) " +
                "EXCEPT " +
                "(SELECT s.store_index, s.name, s.img_url " +
                "FROM jokbo j " +
                "JOIN store s ON j.store_index = s.store_index " +
                "JOIN users u ON j.user_index = u.user_index " +
                "WHERE u.department = ? " +
                "GROUP BY s.store_index, s.name, s.img_url, u.department " +
                "ORDER BY COUNT(j.jokbo_index) DESC)" +
                "LIMIT 3";

        Query nativeQuery = em.createNativeQuery(sql)
                .setParameter(1, department);

        List<Object[]> resultList = nativeQuery.getResultList();
        List<MatdoriPick> matdoriPicks = new ArrayList<>();
        for(Object[] row : resultList) {
            MatdoriPick matdoriPick = new MatdoriPick(Long.valueOf(row[0].toString()), row[1].toString(), row[2].toString());
            matdoriPicks.add(matdoriPick);
        }

        return matdoriPicks;
    }

    /**
     * 족보 부자 가게 리스트 조회하기
     */
    public List<JokboRichStore> getJokboRichStores() {
        return em.createQuery("SELECT new com.matdori.matdori.repositoy.Dto.JokboRichStore(s.id, s.name, s.imgUrl, s.jokbos.size) " +
                        "FROM Jokbo j " +
                        "JOIN j.store s " +
                        "WHERE j.createdAt between :startTime and :endTime " +
                        "GROUP BY s.id, s.name, s.imgUrl, s.jokbos.size " +
                        "ORDER BY s.jokbos.size DESC ", JokboRichStore.class)
                .setParameter("startTime", LocalDateTime.now().minusDays(30))
                .setParameter("endTime", LocalDateTime.now())
                .setMaxResults(3)
                .getResultList();
    }

    public List<com.matdori.matdori.repositoy.Dto.StoreListByCategory> findByCategory(StoreCategory category, int startIndex){
        return em.createQuery(
                        "SELECT new com.matdori.matdori.repositoy.Dto.StoreListByCategory(s.id, s.name, " +
                                "AVG(j.flavorRating) , AVG(j.cleanRating) ,AVG(j.underPricedRating), s.imgUrl, s.jokbos.size) " +
                                "FROM Store s " +
                                "LEFT JOIN s.jokbos j " +
                                "GROUP BY s.name,s.category, s.id, s.imgUrl " +
                                "HAVING s.category =:category " +
                                "ORDER BY s.id", StoreListByCategory.class)
                .setParameter("category" , category)
                .setFirstResult((startIndex-1)*15)
                .setMaxResults(15)
                .getResultList();
    }

    public com.matdori.matdori.repositoy.Dto.StoreRatings getAllRatings(Store store) {
        return em.createQuery(
                "SELECT new com.matdori.matdori.repositoy.Dto.StoreRatings(" +
                        "AVG(j.flavorRating), " +
                        "AVG(j.underPricedRating), " +
                        "AVG(j.cleanRating)) " +
                        "FROM Jokbo j " +
                        "WHERE j.store =: store ", StoreRatings.class)
                .setParameter("store", store)
                .getSingleResult();
    }
}
