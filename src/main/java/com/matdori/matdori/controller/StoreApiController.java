package com.matdori.matdori.controller;

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

import javax.validation.Valid;

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
    @Data
    @AllArgsConstructor
    static class StoreInformationResponse{
        OpenHours time;
        String phone_number;
        String address;
        String comment;
    }

    @Data
    @AllArgsConstructor
    static class StoreMenuResponse{

    }
}
