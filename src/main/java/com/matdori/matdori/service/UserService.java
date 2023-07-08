package com.matdori.matdori.service;

import com.matdori.matdori.domain.*;
import com.matdori.matdori.exception.*;
import com.matdori.matdori.repositoy.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Member;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StoreFavoriteRepository storeFavoriteRepository;
    private final StoreRepository storeRepository;
    private final JokboRepository jokboRepository;
    private final JokboCommentRepository jokboCommentRepository;

    public User findOne(Long userId) { return userRepository.findOne(userId); }
    @Transactional
    public void signUp(User user){
        // 이메일 형식 체크
        if(!user.getEmail().matches("[0-9]{8}@inha.ac.kr"))
            throw new InvalidEmailException(ErrorCode.INVALID_EMAIL_FORMAT);
        // 회원 중복 체크
        Optional<User> duplicatedUser =  userRepository.findByEmail(user.getEmail());
        if(duplicatedUser.isPresent())
            throw new DuplicatedUserException(ErrorCode.DUPLICATED_USER);

        userRepository.save(user);
    }
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

    @Transactional
    public void updatePassword(Long userId, String password) throws NoSuchAlgorithmException {
        User user = userRepository.findOne(userId);
        user.setPassword(UserSha256.encrypt(password));
    }

    @Transactional
    public void updateNickname(Long userId, String nickname){
        // 중복 닉네임 예외처리 필요
        User user = userRepository.findOne(userId);
        user.setNickname(nickname);
    }

    public List<Jokbo> readAllMyJokbo(Long userId){ return jokboRepository.findByUserIndex(userId);}
    public List<JokboComment> readAllMyJokboComment(Long userId){ return jokboCommentRepository.findByUserIndex(userId);}
}

