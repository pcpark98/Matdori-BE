package com.matdori.matdori.controller;

import com.matdori.matdori.domain.Category;
import com.matdori.matdori.domain.Menu;
import com.matdori.matdori.domain.OpenHours;
import com.matdori.matdori.domain.Store;
import com.matdori.matdori.service.StoreService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class StoreApiController {

    private final StoreService storeService;

    // 별점을 가져와야 하므로
    // 족보가 완성되면 작성
    //@GetMapping("/stores/{storeIndex}/info-header")
    //public

    @GetMapping("/stores/{storeIndex}/information")
    public StoreInformationResponse readStoreInformation(@PathVariable("storeIndex") Long id){
        Store store = storeService.findOne(id);
        return new StoreInformationResponse(store.getOpenHours(), store.getPhoneNumber(), store.getAddress(), store.getComment());
    }

    @GetMapping("/stores/{storeIndex}/menu")
    public List<StoreMenuResponse> readStoreMenu(@PathVariable("storeIndex") Long id){
        List<Category> Categories = storeService.findAllCategoryWithMenu(id);
        System.out.println(Categories.size());
        List<StoreMenuResponse> collect = Categories.stream().map(c -> new StoreMenuResponse(c))
                .collect(Collectors.toList());
        return collect;
    }

    @Data
    @AllArgsConstructor
    static class StoreMenuResponse{
        String name;
        List<MenuDto> menus = new ArrayList<>();
        public StoreMenuResponse(Category category) {
            this.name = category.getName();
            this.menus = category.getMenus().stream()
                    .map(c -> new MenuDto(c))
                    .collect(Collectors.toList());
        }
    }
    @Data
    @AllArgsConstructor
     static class MenuDto{
        private String name;
        private Integer price;
        private String img_url;
        public MenuDto(Menu menu) {
            this.name = menu.getName();
            this.price = menu.getPrice();
            this.img_url = menu.getImg_url();
        }
    }

    @Data
    @AllArgsConstructor
    static class StoreInformationResponse{
        OpenHours time;
        String phone_number;
        String address;
        String comment;
    }
}
