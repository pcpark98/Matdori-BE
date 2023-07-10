package com.matdori.matdori.service;

import com.matdori.matdori.domain.*;
import com.matdori.matdori.exception.*;
import com.matdori.matdori.repositoy.*;
import com.matdori.matdori.util.SessionUtil;
import com.matdori.matdori.util.UserUtil;
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
    private final JokboFavoriteRepository jokboFavoriteRepository;

    public User findOne(Long userId) { return userRepository.findOne(userId); }
    @Transactional
    public void signUp(User user) throws NoSuchAlgorithmException {
        // 이메일 형식 체크
        if(!UserUtil.isValidEmailFormat(user.getEmail()))
            throw new InvalidEmailException(ErrorCode.INVALID_EMAIL_FORMAT);

        if(!UserUtil.isValidPasswordFormat(user.getPassword()))
            throw new InvalidEmailException(ErrorCode.INVALID_PASSWORD_FORMAT);

        // password 암호화
        user.setPassword(UserSha256.encrypt(user.getPassword()));
        // 회원 중복 체크
        Optional<User> duplicatedUser =  userRepository.findByEmail(user.getEmail());
        if(duplicatedUser.isPresent())
            throw new DuplicatedUserException(ErrorCode.DUPLICATED_USER);

        userRepository.save(user);
    }
    public List<StoreFavorite> findAllFavoriteStore(Long userId) { return storeFavoriteRepository.findAllFavoriteStore(userId);}

    public List<JokboFavorite> findAllFavoriteJokbo(Long userId) { return jokboFavoriteRepository.findAllFavoriteJokbo(userId);}
    @Transactional
    public void deleteFavoriteStore(Long favoriteStoreId) { storeFavoriteRepository.deleteStoreFavorite(favoriteStoreId);}

    @Transactional
    public void deleteFavoriteJokbo(Long favoriteJokboId) { jokboFavoriteRepository.delete(favoriteJokboId);}
    @Transactional
    public void createFavoriteStore(Long storeId, Long userId) {
        User user = userRepository.findOne(userId);
        Store store = storeRepository.findOne(storeId);
        StoreFavorite storeFavorite = new StoreFavorite(user, store);
        storeFavoriteRepository.saveStoreFavorite(storeFavorite);
    }

    @Transactional
    public void createFavoriteJokbo(Long jokboId, Long userId){
        User user = userRepository.findOne(userId);
        Jokbo jokbo = jokboRepository.findOne(jokboId);
        jokboFavoriteRepository.save(new JokboFavorite(jokbo, user));
    }

    @Transactional
    public void updatePassword(Long userId, String password) throws NoSuchAlgorithmException {
        if(!UserUtil.isValidPasswordFormat(password))
            throw new InvalidPasswordException(ErrorCode.INVALID_EMAIL_FORMAT);

        User user = userRepository.findOne(userId);
        user.setPassword(UserSha256.encrypt(password));
    }

    @Transactional
    public void updatePasswordWithoutLogin(String email, String password) throws NoSuchAlgorithmException {
        // 비밀번호 형식 체크
        if(!UserUtil.isValidPasswordFormat(password))
            throw new InvalidPasswordException(ErrorCode.INVALID_PASSWORD_FORMAT);
        // 이메일 인증 여부 체크
        EmailAuthorizationType type = EmailAuthorizationType.valueOf(SessionUtil.getAttribute(email));
        if(type == null || type != EmailAuthorizationType.UPDATEPASSWORD)
            throw new IncompleteEmailVerificationException(ErrorCode.INCOMPLETE_EMAIL_VERIFICATION);

        // 비밀번호 변경
        User user = userRepository.findByEmail(email).get();
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

