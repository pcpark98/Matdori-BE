package com.matdori.matdori.repositoy;

import com.matdori.matdori.domain.Category;
import com.matdori.matdori.domain.Store;
import com.matdori.matdori.domain.StoreFavorite;
import com.matdori.matdori.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;
    public User findOne(Long id){ return em.find(User.class, id); }
    public void save(User user) { em.persist(user);}

    public Optional<User> findByEmail(String email){
        return em.createQuery(
                        "SELECT u FROM User u " +
                                "WHERE u.email =: email ", User.class)
                .setParameter("email", email)
                .getResultList()
                .stream()
                .findAny();
    }
    public Optional<User> login(String email, String password){
        return em.createQuery(
                        "SELECT u FROM User u " +
                                "WHERE u.email =: email " +
                                "AND u.password =: password", User.class)
                .setParameter("email", email)
                .setParameter("password", password)
                .getResultList()
                .stream()
                .findAny();
    }
}