package com.matdori.matdori.repositoy.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendedMenu {

    private Long storeId;
    private String storeName;
    private String menuName;
    private Double totalRating;
    private String imgUrl;

    public RecommendedMenu(Long storeId, String storeName, String menuName, String imgUrl, Double flavorRating, Double underPricedRating, Double cleanRating) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.menuName = menuName;
        this.totalRating = getScore(flavorRating) + getScore(underPricedRating) + getScore(cleanRating);
        if(totalRating != 0.0) this.totalRating = Math.ceil(totalRating/3 * 10)/ 10;
        else this.totalRating = 0.0;
        this.imgUrl = imgUrl;
    }
    public Double getScore(Double score) {
        if(score == null) return 0.0;
        else return score;
    }
}



