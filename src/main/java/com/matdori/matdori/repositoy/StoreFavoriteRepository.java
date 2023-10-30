package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.StoreFavorite;
import com.matdori.matdori.repositoy.Dto.FavoriteStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StoreFavoriteRepository {

    private final EntityManager em;

    public StoreFavorite findOne(Long favoriteStoreId) { return em.find(StoreFavorite.class, favoriteStoreId);}
    public Long saveStoreFavorite(StoreFavorite storeFavorite){ em.persist(storeFavorite); return storeFavorite.getId();}

    public void deleteStoreFavorite(List<Long> favoriteStoresId, Long userId) {
        em.createQuery(
                "DELETE FROM StoreFavorite s " +
                        "WHERE s.id IN :favoriteStoresId AND s.user.id = : userId")
                .setParameter("favoriteStoresId", favoriteStoresId)
                .setParameter("userId", userId)
                .executeUpdate();
    }

    public List<FavoriteStore> findAllFavoriteStore(Long userId) {
        return em.createQuery(
                "SELECT new com.matdori.matdori.repositoy.Dto.FavoriteStore(f.id, s.id, s.jokbos.size," +
                        "(SELECT (AVG(j.flavorRating) + AVG(j.cleanRating) + AVG(j.underPricedRating)) /3  FROM Jokbo j WHERE j.store.id = s.id)," +
                        " s.name, s.category, s.imgUrl) FROM User u " +
                        "JOIN u.storeFavorites f " +
                        "JOIN f.store s " +
                        "WHERE u.id =: id " +
                        "ORDER BY f.id DESC ", FavoriteStore.class)
                .setParameter("id",userId)
                .setMaxResults(14)
                .getResultList();
    }

    public List<FavoriteStore> getFavoriteStoresDescendingById(Long userId, Long favoriteStoreId) {
        return em.createQuery(
                        "SELECT new com.matdori.matdori.repositoy.Dto.FavoriteStore(f.id, s.id, s.jokbos.size," +
                                "(SELECT (AVG(j.flavorRating) + AVG(j.cleanRating) + AVG(j.underPricedRating)) /3  FROM Jokbo j WHERE j.store.id = s.id)," +
                                " s.name, s.category, s.imgUrl) FROM User u " +
                                "JOIN u.storeFavorites f " +
                                "JOIN f.store s " +
                                "WHERE u.id =: id AND f.id < :favoriteStoreId " +
                                "ORDER BY f.id DESC ", FavoriteStore.class)
                .setParameter("id",userId)
                .setParameter("favoriteStoreId",favoriteStoreId)
                .setMaxResults(14)
                .getResultList();
    }
}
