package com.matdori.matdori.repositoy.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreRatings {

    private Double totalRating;
    private Double flavorRating;
    private Double underPricedRating;
    private Double cleanRating;

    public StoreRatings(Double flavorRating, Double underPricedRating, Double cleanRating) {

        Double totalRating =getScore(flavorRating) + getScore(underPricedRating) + getScore(cleanRating);
        if(totalRating != 0.0) this.totalRating = totalRating/3;
        else this.totalRating = 0.0;

        this.flavorRating = flavorRating;
        this.underPricedRating = underPricedRating;
        this.cleanRating = cleanRating;
    }

    public Double getScore(Double score) {
        if(score == null) return 0.0;
        else return score;
    }
}
