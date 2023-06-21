package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.Category;
import com.matdori.matdori.domain.Store;
import com.matdori.matdori.domain.StoreFavorite;
import com.matdori.matdori.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;
    public User findOne(Long id){ return em.find(User.class, id); }

}
