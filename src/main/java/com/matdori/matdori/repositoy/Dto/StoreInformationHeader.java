package com.matdori.matdori.repositoy.Dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
public class StoreInformationHeader{
    private String name;
    private Double totalRating;
    private Double flavorRating;
    private Double underPricedRating;
    private Double cleanRating;
    private String imgUrl;

    public StoreInformationHeader(String name, Double flavorRating, Double underPricedRating, Double cleanRating, String imgUrl) {
        this.name = name;
        this.totalRating = Math.round ((flavorRating + underPricedRating + cleanRating) /3 * 10.0) / 10.0;
        this.flavorRating = flavorRating;
        this.underPricedRating = underPricedRating;
        this.cleanRating = cleanRating;
        this.imgUrl = imgUrl;
    }
}


