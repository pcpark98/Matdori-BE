package com.matdori.matdori.repositoy.Dto;

import com.matdori.matdori.domain.Jokbo;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

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
        Double totalRating = getScore(flavorRating) +getScore(underPricedRating) + getScore(cleanRating);
        if(totalRating != 0.0) this.totalRating = totalRating/3;
        else this.totalRating = 0.0;
        this.flavorRating = getScore(flavorRating);
        this.underPricedRating = getScore(underPricedRating);
        this.cleanRating = getScore(cleanRating);
        this.imgUrl = imgUrl;
    }
    public Double getScore(Double score){
        if(score == null)
            return 0.0;
        else return score;
    }
}


