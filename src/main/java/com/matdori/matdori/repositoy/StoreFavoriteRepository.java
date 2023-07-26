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
    public void saveStoreFavorite(StoreFavorite storeFavorite){ em.persist(storeFavorite);}

    public void deleteStoreFavorite(Long id) { em.remove(em.find(StoreFavorite.class, id));}

    public List<StoreFavorite> findAllFavoriteStore(Long id, int pageCount) {
        return em.createQuery(
                "SELECT f FROM User u " +
                        "JOIN u.storeFavorites f " +
                        "JOIN FETCH f.store s " +
                        "WHERE u.id =: id", StoreFavorite.class
        ).setParameter("id",id)
                .setFirstResult((pageCount-1)*15)
                .getResultList();
    }
}
