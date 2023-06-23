package com.matdori.matdori.controller;

import com.matdori.matdori.domain.Store;
import com.matdori.matdori.domain.StoreFavorite;
import com.matdori.matdori.domain.User;
import com.matdori.matdori.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Member;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    // 회원 가입
    @PostMapping("/sign-up")
    public void createUser(@RequestBody @Valid CreateUserRequest request){
        User user = new User();
        user.setEmail(request.email);
        user.setDepartment("학과 parsing 필요");
        // sha256으로 encrypt 필요
        user.setPassword(request.password);
        user.setNickname("맛도리1234");
        // 약관 동의 추가하는 로직 필요
        userService.signUp(user);
    }

    // 가게 좋아요 누르기
    @PostMapping("/users/{userIndex}/favorite-store")
    public void createFavoriteStore(@PathVariable("userIndex") Long userId,
                                    @RequestBody @Valid CreateFavoriteStoreRequest request){
        userService.createFavoriteStore(request.storeId, userId);
    }

    // 내가 좋아요한 가게 리스트 조회
    @GetMapping("/users/{userIndex}/favorite-stores")
    public List<readFavoriteStoresResponse> readFavoriteStores(@PathVariable("userIndex") Long id,
                                   @RequestParam Long pageCount){
        List<StoreFavorite> FavoriteStores = userService.findAllFavoriteStore(id);
        return FavoriteStores.stream()
                .map(s -> new readFavoriteStoresResponse(s.getId(), s.getStore().getId(), s.getStore().getName(), s.getStore().getImg_url()))
                .collect(Collectors.toList());
    }

    // 내가 좋아요한 가게 삭제
    @DeleteMapping("/users/{userIndex}/favorite-stores/{favoriteStoreIndex}")
    public void deleteFavoriteStore(@PathVariable("userIndex") Long userId,
                                    @PathVariable("favoriteStoreIndex") Long storeId){
        // TODO
        // 유저 과정 인증 필요
        userService.deleteFavoriteStore(storeId);
    }

    @Data
    @AllArgsConstructor
    static class CreateFavoriteStoreRequest{
        private Long storeId;
    }

    @Data
    @AllArgsConstructor
    static class readFavoriteStoresResponse{
        private Long favoriteStoreId;
        private Long storeId;
        private String name;
        private String imgUrl;
    }

    @Data
    @AllArgsConstructor
    static class CreateUserRequest{
        private String email;
        private String password;
        private int[] termIndex;
    }
}
