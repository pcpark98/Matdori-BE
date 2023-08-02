package com.matdori.matdori.domain;

import com.matdori.matdori.exception.ErrorCode;
import com.matdori.matdori.exception.NotExistStoreCategoryException;
import com.matdori.matdori.exception.NotExistStoreException;

public enum StoreCategory {
    KOREAN_CUISINE("한식"),
    CHINESE_CUISINE("중식"),
    JAPANESE_CUISINE("일식"),
    DESSERT_COFFEE("디저트,커피"),
    CHICKEN("치킨"),
    FAST_FOOD("패스트푸드"),
    PUB("술집"),
    WESTERN_CUISINE("양식"),
    MEAT("고기"),
    CASUAL_FOOD("분식"),
    LAST_NIGHT_SNACK("야식"),
    SALAD("샐러드"),
    ASIAN("아시안"),
    MEAL_KIT("밀키트"),
    ETC("기타");

    private final String name;

    private StoreCategory(String name){
        this.name = name;
    }

    private String getName(){ return this.name;}

    // 한글명을 enum 명으로 바꿔주는 함수
    public static StoreCategory nameOf(String name){
        for(StoreCategory _name : StoreCategory.values()){
            if(_name.getName().equals(name))
                return _name;
        }
        throw new NotExistStoreCategoryException(ErrorCode.NOT_EXISTED_STORE_CATEGORY);
    }
}
