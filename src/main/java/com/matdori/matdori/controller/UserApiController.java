package com.matdori.matdori.controller;

import com.matdori.matdori.domain.StoreFavorite;
import com.matdori.matdori.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping("/users/{userIndex}/favorite-store")
    public void createFavoriteStore(@PathVariable("userIndex") Long userId,
                                    @RequestBody @Valid CreateFavoriteStoreRequest request){
        userService.createFavoriteStore(request.storeId, userId);
    }

    @Data
    static class CreateFavoriteStoreRequest{
        private Long storeId;
    }
}
