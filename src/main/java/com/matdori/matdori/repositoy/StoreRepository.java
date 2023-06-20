package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.Menu;
import com.matdori.matdori.domain.Store;
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

    public List<Menu> findAllMenuWithCategory(){
        return em.createQuery(
                "SELECT m FROM Menu m " +
                        "JOIN FETCH m.Category c " +
                        "JOIN FETCH c.Store s" +
                        " WHERE s.store_index = :id", Menu.class
        ).getResultList();
    }


}
