package com.matdori.matdori.repositoy.Dto;

import com.matdori.matdori.domain.StoreCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreListByCategory {
    private Long storeId;
    private String name;
    private String category;
    private Double totalRating;
    private String imgUrl;
    private Integer jokboCnt;

    public StoreListByCategory(Long storeId, String name, StoreCategory category, Double flavorRating, Double underPricedRating, Double cleanRating, String imgUrl, Integer jokboCnt) {
        this.storeId =storeId;
        this.name = name;
        this.category = category.getName();
        Double totalRating = getScore(flavorRating) +getScore(underPricedRating) + getScore(cleanRating);
        if(totalRating != 0.0) this.totalRating = totalRating/3;
        else this.totalRating = 0.0;
        this.imgUrl = imgUrl;
        this.jokboCnt = jokboCnt;
    }
    public Double getScore(Double score){
        if(score == null)
            return 0.0;
        else return score;
    }
}
