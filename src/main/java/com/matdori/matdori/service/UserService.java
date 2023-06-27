package com.matdori.matdori.service;

import com.matdori.matdori.domain.Store;
import com.matdori.matdori.domain.StoreFavorite;
import com.matdori.matdori.domain.User;
import com.matdori.matdori.repositoy.StoreFavoriteRepository;
import com.matdori.matdori.repositoy.StoreRepository;
import com.matdori.matdori.repositoy.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Member;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StoreFavoriteRepository storeFavoriteRepository;
    private final StoreRepository storeRepository;

    public User findOne(Long userId) { return userRepository.findOne(userId); }
    @Transactional
    public void signUp(User user){ userRepository.save(user);}
    public List<StoreFavorite> findAllFavoriteStore(Long userId) { return storeFavoriteRepository.findAllFavoriteStore(userId);}
    @Transactional
    public void deleteFavoriteStore(Long favoriteStoreId) { storeFavoriteRepository.deleteStoreFavorite(favoriteStoreId);}

    @Transactional
    public void createFavoriteStore(Long storeId, Long userId) {
        User user = userRepository.findOne(userId);
        Store store = storeRepository.findOne(storeId);
        StoreFavorite storeFavorite = StoreFavorite.createStoreFavorite(user, store);
        storeFavoriteRepository.saveStoreFavorite(storeFavorite);
    }

    public Optional<User> login(String email, String password){ return userRepository.login(email, password);}
}
