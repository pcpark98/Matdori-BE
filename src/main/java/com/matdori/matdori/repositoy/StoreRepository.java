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
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    public List<Store> getStoreListByDepartment(Department department) {

        return em.createQuery(
                "SELECT s FROM Store s " +
                        "JOIN Jokbo j ON j.store.id = s.id " +
                        "WHERE j.user.department =: department " +
                        "GROUP BY s.id " +
                        "ORDER BY COUNT(j.id) DESC ", Store.class)
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
                        "SELECT new com.matdori.matdori.repositoy.Dto.StoreInformationHeader(s.name, s.category, " +
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
    public List<Store> getMatdoriPick() {

        return em.createQuery(
                "SELECT s FROM Store s " +
                        "ORDER BY RANDOM()", Store.class)
                .setMaxResults(3)
                .getResultList();
    }

    /**
     * 족보 부자 가게 리스트 조회하기
     */
    public List<JokboRichStore> getJokboRichStores() {
        return em.createQuery("SELECT new com.matdori.matdori.repositoy.Dto.JokboRichStore(s.id, s.name, s.imgUrl, s.jokbos.size) " +
                        "FROM Jokbo j " +
                        "JOIN j.store s ON j.createdAt BETWEEN :startTime and : endTime " +
                        "GROUP BY s.id, s.name, s.imgUrl " +
                        "ORDER BY COUNT(j) DESC ", JokboRichStore.class)
                .setParameter("startTime", LocalDateTime.now().minusDays(30))
                .setParameter("endTime", LocalDateTime.now())
                .setMaxResults(3)
                .getResultList();
    }

    public List<com.matdori.matdori.repositoy.Dto.StoreListByCategory> findByCategory(StoreCategory category, Long cursor){
        return em.createQuery(
                        "SELECT new com.matdori.matdori.repositoy.Dto.StoreListByCategory(s.id, s.name, s.category, " +
                                "AVG(j.flavorRating) , AVG(j.cleanRating) ,AVG(j.underPricedRating), s.imgUrl, s.jokbos.size) " +
                                "FROM Store s " +
                                "LEFT JOIN s.jokbos j " +
                                "WHERE s.category =:category AND s.id < :cursor " +
                                "GROUP BY s.name,s.category, s.id, s.imgUrl " +
                                "ORDER BY s.id DESC ", StoreListByCategory.class)
                .setParameter("category" , category)
                .setParameter("cursor", cursor)
                .setMaxResults(14)
                .getResultList();
    }

    public List<com.matdori.matdori.repositoy.Dto.StoreListByCategory> getCategoryStoresDescendingById(StoreCategory category){
        return em.createQuery(
                        "SELECT new com.matdori.matdori.repositoy.Dto.StoreListByCategory(s.id, s.name, s.category, " +
                                "AVG(j.flavorRating) , AVG(j.cleanRating) ,AVG(j.underPricedRating), s.imgUrl, s.jokbos.size) " +
                                "FROM Store s " +
                                "LEFT JOIN s.jokbos j " +
                                "WHERE s.category =:category " +
                                "GROUP BY s.name,s.category, s.id, s.imgUrl " +
                                "ORDER BY s.id DESC ", StoreListByCategory.class)
                .setParameter("category" , category)
                .setMaxResults(14)
                .getResultList();
    }

    public Long countStoreJokbo(Long storeId){
        return em.createQuery(
                        "SELECT COUNT(*) " +
                                "FROM Jokbo j " +
                                "WHERE j.store.id =: storeId", Long.class)
                .setParameter("storeId", storeId)
                .getSingleResult();
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

    public Long countStoresByCategory(StoreCategory storeCategory) {
        return em.createQuery(
                "SELECT COUNT(*) " +
                        "FROM Store s " +
                        "WHERE s.category =: storeCategory", Long.class)
                .setParameter("storeCategory", storeCategory)
                .getSingleResult();
    }

    public List<RecommendedStore> getRecommendedStore(){
        return em.createQuery(
                        "SELECT new com.matdori.matdori.repositoy.Dto.RecommendedStore(s.id, s.name, s.imgUrl, AVG(j.flavorRating), AVG(j.underPricedRating),AVG(j.cleanRating)) " +
                                "FROM Store s LEFT JOIN s.jokbos j " +
                                "WHERE s.category NOT IN :categories " +
                                "GROUP BY s.id, s.name, s.imgUrl " +
                                "ORDER BY RANDOM()", RecommendedStore.class)
                .setParameter("categories", Arrays.asList(StoreCategory.ETC, StoreCategory.DESSERT_COFFEE, StoreCategory.MEAL_KIT, StoreCategory.PUB))
                .setMaxResults(3)
                .getResultList();
    }

    public List<RecommendedMenu> getRecommendedMenu() {
        // 한 번에 가져오도록 쿼리를 날리면 불필요하게 join이 많이 돼서 쿼리를 쪼갰음.

        // 첫 번째 쿼리: 가게와 메뉴를 가져오는 쿼리
        TypedQuery<Object[]> menuQuery = em.createQuery(
                        "SELECT s.id, s.name, m.name, s.imgUrl, m.category.id " +
                                "FROM Store s " +
                                "JOIN Category c ON c.store.id = s.id " +
                                "JOIN Menu m ON c.id = m.category.id " +
                                "WHERE s.category NOT IN :categories " +
                                "ORDER BY RANDOM()",
                        Object[].class)
                .setParameter("categories", Arrays.asList(StoreCategory.ETC, StoreCategory.DESSERT_COFFEE, StoreCategory.MEAL_KIT, StoreCategory.PUB))
                .setMaxResults(3);

        // 첫 번째 쿼리 실행
        List<Object[]> menuResults = menuQuery.getResultList();

        // 두 번째 쿼리: 족보의 평균값을 가져오는 쿼리
        TypedQuery<Object[]> jokboQuery = em.createQuery(
                        "SELECT s.id, AVG(j.flavorRating), AVG(j.underPricedRating), AVG(j.cleanRating) " +
                                "FROM Store s " +
                                "LEFT JOIN s.jokbos j " +
                                "WHERE s.id IN :storeIds " +
                                "GROUP BY s.id",
                        Object[].class)
                .setParameter("storeIds", menuResults.stream()
                        .map(data -> (Long) data[0])
                        .collect(Collectors.toList()));

        // 두 번째 쿼리 실행
        List<Object[]> jokboResults = jokboQuery.getResultList();

        // 결과를 합쳐서 RecommendedMenu 리스트 생성
        List<RecommendedMenu> recommendedMenus = new ArrayList<>();
        for (int i = 0; i < menuResults.size(); i++) {
            Object[] menuData = menuResults.get(i);
            Object[] jokboData = jokboResults.get(i);

            RecommendedMenu recommendedMenu = new RecommendedMenu(
                    (Long) menuData[0],
                    (String) menuData[1],
                    (String) menuData[2],
                    (String) menuData[3],
                    (Double) jokboData[1],
                    (Double) jokboData[2],
                    (Double) jokboData[3]
            );

            recommendedMenus.add(recommendedMenu);
        }

        return recommendedMenus;

    }

    /***
     * private Long storeIndex;
     *     private String name;
     *     private Double totalRating;
     *     private Double flavorRating;
     *     private Double cleanRating;
     *     private Double underPricedRating;
     *     private String imgUrl;
     
     */

    public List<MatdoriTop3> getMatdoriTop3(String order){
        // order를 enum으로 바꾸는 작업 필요
        // 진짜 개별로군..
        String orderByClause = "ORDER BY (AVG(j.flavorRating) + AVG(j.cleanRating) + AVG(j.underPricedRating)) / 3 DESC";

        switch (order){
            case "음식 맛" :
                orderByClause = "ORDER BY AVG(j.flavorRating) DESC";
                break;
            case "가성비" :
                orderByClause = "ORDER BY AVG(j.underPricedRating) DESC";
                break;
            case "청결" :
                orderByClause = "ORDER BY AVG(j.cleanRating) DESC";
        }

        return em.createQuery(
                "SELECT new com.matdori.matdori.repositoy.Dto.MatdoriTop3" +
                        "(s.id, s.name, " +
                        "AVG(j.flavorRating), AVG(j.cleanRating), AVG(j.underPricedRating), s.imgUrl) " +
                        "FROM Store s LEFT JOIN s.jokbos j " +
                        "GROUP BY s.id, s.name, s.imgUrl " +
                        orderByClause + " NULLS LAST ", MatdoriTop3.class
        ).setMaxResults(3)
                .getResultList();
    }
}
