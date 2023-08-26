package com.matdori.matdori.repositoy.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatdoriTop3 {

    private Long storeIndex;
    private String name;
    private Double totalRating;
    private Double flavorRating;
    private Double cleanRating;
    private Double underPricedRating;
    private String imgUrl;

    public MatdoriTop3(Long storeIndex, String name, Double flavorRating, Double cleanRating, Double underPricedRating, String imgUrl) {
        this.storeIndex = storeIndex;
        this.name = name;
        this.flavorRating = Math.ceil(getValue(flavorRating)* 10)/10;
        this.cleanRating = Math.ceil(getValue(cleanRating)* 10)/10;
        this.underPricedRating = Math.ceil(getValue(underPricedRating)* 10)/10;
        Double totalSum = this.flavorRating + this.cleanRating + this.underPricedRating;

        if(totalSum.equals(0))
            this.totalRating = 0.0;
        else
            this.totalRating = Math.ceil( totalSum/3 * 10)/ 10;
        this.imgUrl = imgUrl;
    }


    public Double getValue(Double value){
        if(value == null)
            return 0.0;
        else return value;
    }
}
