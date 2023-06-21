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
    public void saveStoreFavorite(StoreFavorite storeFavorite){ em.persist(storeFavorite);}
    public List<Store> findAllFavoriteStore(Long id) {
        return em.createQuery(
                "SELECT s FROM User u " +
                        "JOIN FETCH u.storeFavorites f " +
                        "JOIN FETCH f.store s " +
                        "WHERE u.id =: id", Store.class
        ).setParameter("id",id).getResultList();
    }
}
