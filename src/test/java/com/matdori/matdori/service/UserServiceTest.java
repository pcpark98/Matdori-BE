package com.matdori.matdori.service;

import com.matdori.matdori.domain.*;
import com.matdori.matdori.repositoy.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired UserService userService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    JokboRepository jokboRepository;
    @Autowired
    JokboCommentRepository jokboCommentRepository;
    @Autowired
    JokboCommentFavoriteRepository jokboCommentFavoriteRepository;


    @Test
    public void 댓글_좋아요_여부_확인() {

        // given

        // 유저 정보 저장
        User user = new User();
        user.setEmail("matdori@gmail.com");
        user.setPassword("1234");
        user.setNickname("testNickname");
        user.setDepartment(Department.COMPUTER_ENGINEERING);

        User user2 = new User();
        user2.setEmail("matdori2@gmail.com");
        user2.setPassword("1234");
        user2.setNickname("testNickname2");
        user2.setDepartment(Department.INFORMATION_AND_COMMUNICATION_ENGINEERING);

        userRepository.save(user);
        userRepository.save(user2);


        // 가게 정보 저장
        Store store = new Store();
        store.setName("가게 이름");
        store.setCategory(StoreCategory.CHICKEN);
        storeRepository.save(store);


        // 족보 정보 저장
        Jokbo jokbo = new Jokbo();
        jokbo.setUser(user);
        jokbo.setStore(store);
        jokbo.setTitle("족보 제목");
        jokbo.setContents("족보 내용");
        jokbo.setFlavorRating(3);
        jokbo.setUnderPricedRating(3);
        jokbo.setCleanRating(3);
        jokboRepository.save(jokbo);


        // 족보 댓글 정보 버장
        JokboComment jokboComment = new JokboComment();
        jokboComment.setJokbo(jokbo);
        jokboComment.setUser(user);
        jokboComment.setContents("댓글 내용");
        jokboComment.setIsDeleted(false);
        jokboCommentRepository.save(jokboComment);


        // when
        userService.createFavoriteComment(jokboComment.getId(), user2.getId());

        Long favoriteCommentId = userService.getFavoriteCommentId(user.getId(), jokboComment.getId());
        Long favoriteCommentId2 = userService.getFavoriteCommentId(user2.getId(), jokboComment.getId());


        // then
        assertNull(favoriteCommentId);
        assertNotNull(favoriteCommentId2);
    }


    @Test
    public void 댓글_좋아요_취소() {

        // given

        // 유저 정보 저장
        User user = new User();
        user.setEmail("matdori@gmail.com");
        user.setPassword("1234");
        user.setNickname("testNickname");
        user.setDepartment(Department.COMPUTER_ENGINEERING);

        userRepository.save(user);


        // 가게 정보 저장
        Store store = new Store();
        store.setName("가게 이름");
        store.setCategory(StoreCategory.CHICKEN);
        storeRepository.save(store);


        // 족보 정보 저장
        Jokbo jokbo = new Jokbo();
        jokbo.setUser(user);
        jokbo.setStore(store);
        jokbo.setTitle("족보 제목");
        jokbo.setContents("족보 내용");
        jokbo.setFlavorRating(3);
        jokbo.setUnderPricedRating(3);
        jokbo.setCleanRating(3);
        jokboRepository.save(jokbo);


        // 족보 댓글 정보 버장
        JokboComment jokboComment = new JokboComment();
        jokboComment.setJokbo(jokbo);
        jokboComment.setUser(user);
        jokboComment.setContents("댓글 내용");
        jokboComment.setIsDeleted(false);
        jokboCommentRepository.save(jokboComment);


        // when
        Long favoriteCommentId = userService.createFavoriteComment(jokboComment.getId(), user.getId());

        userService.deleteFavoriteComment(favoriteCommentId, user.getId());

        Long favoriteCommentIdAfterDelete = userService.getFavoriteCommentId(user.getId(), jokboComment.getId());


        // then
        assertNull(favoriteCommentIdAfterDelete);
    }
}