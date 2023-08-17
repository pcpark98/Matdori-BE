package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.Store;
import com.matdori.matdori.domain.StoreFavorite;
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

    public void deleteStoreFavorite(Long id) { em.remove(em.find(StoreFavorite.class, id));}

    public List<StoreFavorite> findAllFavoriteStore(Long userId) {
        return em.createQuery(
                "SELECT f FROM User u " +
                        "JOIN u.storeFavorites f " +
                        "JOIN FETCH f.store s " +
                        "WHERE u.id =: id " +
                        "ORDER BY f.id DESC ", StoreFavorite.class)
                .setParameter("id",userId)
                .setMaxResults(14)
                .getResultList();
    }

    public List<StoreFavorite> getFavoriteStoresDescendingById(Long userId, Long favoriteStoreId) {
        return em.createQuery(
                        "SELECT f FROM User u " +
                                "JOIN u.storeFavorites f " +
                                "JOIN FETCH f.store s " +
                                "WHERE u.id =: id AND f.id < :favoriteStoreId " +
                                "ORDER BY f.id DESC ", StoreFavorite.class)
                .setParameter("id",userId)
                .setParameter("favoriteStoreId", favoriteStoreId)
                .setMaxResults(14)
                .getResultList();
    }
}
