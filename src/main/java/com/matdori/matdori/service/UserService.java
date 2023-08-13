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
    private final TermsAgreementRepository termsAgreementRepository;
    private final TermsOfServiceRepository termsOfServiceRepository;

    public User findOne(Long userId) { return userRepository.findOne(userId); }

    /**
     * 회원가입
     */
    @Transactional
    public void signUp(User user) throws NoSuchAlgorithmException {
        // 이메일 형식 체크
        if(!UserUtil.isValidEmailFormat(user.getEmail()))
            throw new InvalidEmailException(ErrorCode.INVALID_EMAIL_FORMAT);

        // 패스워드 형식 체크
        if(!UserUtil.isValidPasswordFormat(user.getPassword()))
            throw new InvalidEmailException(ErrorCode.INVALID_PASSWORD_FORMAT);

        // password 암호화
        user.setPassword(UserSha256.encrypt(user.getPassword()));

        // 회원 중복 체크
        Optional<User> duplicatedUser =  userRepository.findByEmail(user.getEmail());
        if(duplicatedUser.isPresent())
            // 중복된 유저가 있는 경우
            throw new DuplicatedUserException(ErrorCode.DUPLICATED_USER);

        userRepository.save(user);

        // 활성화 되어 있는 약관 불러오기
        List<TermsOfService> allTerms = termsOfServiceRepository.findAllTerms();

        // 동의한 약관 하나하나에 맞게 저장
        for(TermsOfService terms : allTerms){
            termsAgreementRepository.save(new TermAgreement(user, terms));
        }
    }

    /**
     * 내가 좋아요 누른 가게 리스트 조회하기.
     */
    public List<StoreFavorite> findAllFavoriteStore(Long userId, int pageCount) { return storeFavoriteRepository.findAllFavoriteStore(userId, pageCount);}

    public List<JokboFavorite> findAllFavoriteJokbo(Long userId, int pageCount) { return jokboFavoriteRepository.findAllFavoriteJokbo(userId, pageCount);}

    /**
     * 내가 좋아요 누른 가게 삭제하기.
     */
    @Transactional
    public void deleteFavoriteStore(Long favoriteStoreId) {
        if(storeFavoriteRepository.findOne(favoriteStoreId) == null)
            throw new NotExistStoreException(ErrorCode.NOT_EXISTED_STORE);
        storeFavoriteRepository.deleteStoreFavorite(favoriteStoreId);
    }

    @Transactional
    public void deleteFavoriteJokbo(Long favoriteJokboId, Long userId) {
        JokboFavorite jokboFavorite = jokboFavoriteRepository.findOne(favoriteJokboId);
        if(jokboFavorite == null)
            throw new NotExisitedJokboFavoriteException(ErrorCode.NOT_EXISTED_JOKBO_FAVORITE);
        if(jokboFavorite.getUser().getId() != userId)
            throw new InsufficientPrivilegesException(ErrorCode.INSUFFICIENT_PRIVILEGES);
        jokboFavoriteRepository.delete(favoriteJokboId);}

    /**
     * 가게에 좋아요 누르기
     */
    @Transactional
    public Long createFavoriteStore(Long storeId, Long userId) {
        User user = userRepository.findOne(userId);
        Store store = storeRepository.findOne(storeId);
        if(store == null)
            throw new NotExistStoreException(ErrorCode.NOT_EXISTED_STORE);
        StoreFavorite storeFavorite = new StoreFavorite(user, store);
        return storeFavoriteRepository.saveStoreFavorite(storeFavorite);
    }

    /**
     * 족보에 좋아요 누르기
     */
    @Transactional
    public void createFavoriteJokbo(Long jokboId, Long userId){
        User user = userRepository.findOne(userId);

        Optional<Jokbo> jokbo = jokboRepository.findOne(jokboId);
        if(jokbo.isPresent()) jokboFavoriteRepository.save(new JokboFavorite(jokbo.get(), user));
        else throw new NotExistedJokboException(ErrorCode.NOT_EXISTED_JOKBO);
    }

    /**
     * 비밀번호 변경
     *
     */
    @Transactional
    public void updatePassword(Long userId, String password) throws NoSuchAlgorithmException {

        // 비밀번호 형식이 맞는지 확인
        if(!UserUtil.isValidPasswordFormat(password))
            // 형식이 맞지 않은 경우
            throw new InvalidPasswordException(ErrorCode.INVALID_EMAIL_FORMAT);

        User user = userRepository.findOne(userId);
        user.setPassword(UserSha256.encrypt(password));
    }

    /**
     * 비밀번호 찾기(로그인 하지 않은 상태)
     */
    @Transactional
    public void updatePasswordWithoutLogin(String email, String password) throws NoSuchAlgorithmException {
        // 비밀번호 형식 체크
        if(!UserUtil.isValidPasswordFormat(password))
            // 형식이 맞지 않은 경우
            throw new InvalidPasswordException(ErrorCode.INVALID_PASSWORD_FORMAT);

        // 이메일 인증 여부 체크
        EmailAuthorizationType type = EmailAuthorizationType.valueOf(SessionUtil.getAttribute(email));
        if(type == null || type != EmailAuthorizationType.UPDATEPASSWORD)
            // 이메일 인증을 하는 케이스가 잘못된 경우.(비밀번호 찾기를 위한 이메일 인증이 아닌 경우)
            throw new IncompleteEmailVerificationException(ErrorCode.INCOMPLETE_EMAIL_VERIFICATION);

        // 비밀번호 변경
        User user = userRepository.findByEmail(email).get();
        user.setPassword(UserSha256.encrypt(password));
    }

    /**
     * 닉네임 변경
     *
     * 고쳐야 할 부분
     * 1. checkNicknameExistence에서 중복 체크와 업데이트를 한 transaction에 묶기.
     */
    @Transactional
    public void updateNickname(Long userId, String nickname){

        // 닉네임 길이 체크
        if(nickname.length() > 30)
            throw new InvalidNicknameFormatExceition(ErrorCode.INVALID_NICKNAME_FORMAT);
        // 변경하려는 닉네임이 이미 존재하는 닉네임인지 확인
        checkNicknameExistence(nickname);

        User user = userRepository.findOne(userId);
        user.setNickname(nickname);
    }

    /**
     * 내가 쓴 모든 족보 조회하기.
     */
    public List<Jokbo> readAllMyJokbo(Long userId, int pageCount){ return jokboRepository.findByUserIndex(userId, pageCount);}

    /**
     * 내가 쓴 모든 족보 댓글 조회하기.
     */
    public List<JokboComment> readAllMyJokboComment(Long userId, int pageCount){ return jokboCommentRepository.findByUserIndex(userId, pageCount);}

    public void checkNicknameExistence(String nickname) {
        Optional<User> user = userRepository.findByNickname(nickname);
        if(user.isPresent()) throw new DuplicatedNicknameException(ErrorCode.DUPLICATED_NICKNAME);
    }

    /**
     * 유저가 족보에 좋아요를 눌렀는지 확인하기.
     */
    public Long getFavoriteJokboId(Long userId, Long jokboId) {

        Optional<JokboFavorite> jokboFavorite = jokboFavoriteRepository.findByIds(userId, jokboId);
        if(!jokboFavorite.isPresent()) return null;
        else return jokboFavorite.get().getId();
    }

    // 개발 시에 사용할 유저삭제 api
    @Transactional
    public void deleteUser(Long userId){
        termsAgreementRepository.delete(userId);
        userRepository.delete(userId);
    }
}

