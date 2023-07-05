package com.matdori.matdori.controller;


import com.matdori.matdori.domain.Response;
import com.matdori.matdori.domain.StoreFavorite;
import com.matdori.matdori.domain.User;
import com.matdori.matdori.service.UserService;
import com.matdori.matdori.service.UserSha256;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    // 회원 가입
    @PostMapping("/sign-up")
    public void createUser(@RequestBody @Valid CreateUserRequest request) throws NoSuchAlgorithmException {
        User user = new User();
        // email 형식 체크하는 로직 필요
        user.setEmail(request.email);
        user.setDepartment("학과 parsing 필요");
        user.setPassword(UserSha256.encrypt(request.password));
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
    public ResponseEntity<Response<List<readFavoriteStoresResponse>>> readFavoriteStores(@PathVariable("userIndex") Long id,
                                                                                        @RequestParam Long pageCount){
        List<StoreFavorite> FavoriteStores = userService.findAllFavoriteStore(id);
        return ResponseEntity.ok().body(Response.success(FavoriteStores.stream()
                .map(s -> new readFavoriteStoresResponse(s.getId(), s.getStore().getId(), s.getStore().getName(), s.getStore().getImg_url()))
                .collect(Collectors.toList())));
    }

    // 내가 좋아요한 가게 삭제
    @DeleteMapping("/users/{userIndex}/favorite-stores/{favoriteStoreIndex}")
    public void deleteFavoriteStore(@PathVariable("userIndex") Long userId,
                                    @PathVariable("favoriteStoreIndex") Long storeId){
        // TODO
        // 유저 과정 인증 필요
        userService.deleteFavoriteStore(storeId);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody LoginRequest request, HttpSession session) throws NoSuchAlgorithmException {
        String email = request.email;
        String password = UserSha256.encrypt(request.password);
        User user = userService.login(email, password).orElse(null);

        if(user == null){
            /***
             에러 처리 로직 추가해야됨
             */
            return ResponseEntity.ok().body(Response.success(new LoginResponse("로그인 실패")));
        }
        else{
            session.setAttribute("users", user);
            return ResponseEntity.ok().body(Response.success(new LoginResponse(new LoginResult(user.getId(), user.getNickname(), user.getDepartment()))));
        }
    }

    @Data
    static class LoginRequest{
        private String email;
        private String password;
    }

    @Data
    @AllArgsConstructor
    static class LoginResponse<T>{
        private T data;
    }
    @Data
    @AllArgsConstructor
    static class LoginResult{
        private Long userId;
        private String nickname;
        private String department;
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
    static class CreateUserRequest{
        private String email;
        private String password;
        //private int[] termIndex;
    }
}