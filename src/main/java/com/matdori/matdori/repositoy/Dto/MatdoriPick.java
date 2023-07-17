package com.matdori.matdori.repositoy.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatdoriPick {
    private Long storeIndex;
    private String name;
    private String imgUrl;

    public MatdoriPick(Long storeIndex, String name, String imgUrl) {
        this.storeIndex = storeIndex;
        this.name = name;
        this.imgUrl = imgUrl;
    }
}