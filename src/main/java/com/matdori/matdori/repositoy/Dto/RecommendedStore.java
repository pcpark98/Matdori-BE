package com.matdori.matdori.repositoy.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendedStore {
    public RecommendedStore(Long storeId, String name, String imgUrl, Double flavorRating, Double underPricedRating, Double cleanRating) {
        this.storeId = storeId;
        this.name = name;
        this.imgUrl = imgUrl;
        this.totalRating = getScore(flavorRating) + getScore(underPricedRating) + getScore(cleanRating);
        if(totalRating != 0.0) this.totalRating = Math.ceil(totalRating/3);
        else this.totalRating = 0.0;

    }
    public Double getScore(Double score) {
        if(score == null) return 0.0;
        else return score;
    }


    private Long storeId;
    private String name;
    private String imgUrl;
    private Double totalRating;
}
