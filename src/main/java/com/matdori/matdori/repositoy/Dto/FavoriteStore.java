package com.matdori.matdori.repositoy.Dto;

import com.matdori.matdori.domain.StoreCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class FavoriteStore {
    private Long favoriteStoreId;
    private Long storeId;
    private Integer jokboCnt;
    private Double totalRating;
    private String name;
    private String category;
    private String imgUrl;

    public FavoriteStore(Long favoriteStoreId, Long storeId, Integer jokboCnt, Double totalRating, String name, StoreCategory category, String imgUrl) {
        this.favoriteStoreId = favoriteStoreId;
        this.storeId = storeId;
        this.jokboCnt = jokboCnt;
        if(!(totalRating == null || totalRating.equals(0)))
            this.totalRating = Math.ceil(totalRating*10)/10;
        else
            this.totalRating = 0.0D;
        this.name = name;
        this.category = category.getName();
        this.imgUrl = imgUrl;
    }
}