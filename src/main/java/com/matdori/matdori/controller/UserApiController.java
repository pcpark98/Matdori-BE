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
        List<Store> FavoriteStores = userService.findAllFavoriteStore(id);
        return FavoriteStores.stream()
                .map(s -> new readFavoriteStoresResponse(s.getId(), s.getName(), s.getImg_url()))
                .collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    static class CreateFavoriteStoreRequest{
        private Long storeId;
    }

    @Data
    @AllArgsConstructor
    static class readFavoriteStoresResponse{
        private Long StoreId;
        private String name;
        private String imgUrl;
    }
}
