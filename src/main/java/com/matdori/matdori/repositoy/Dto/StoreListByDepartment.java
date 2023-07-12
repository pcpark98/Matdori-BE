package com.matdori.matdori.repositoy.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreListByDepartment {
    private Long storeIndex;
    private String name;
    private String imgUrl;

    public StoreListByDepartment(Long storeIndex, String name, String imgUrl) {
        this.storeIndex = storeIndex;
        this.name = name;
        this.imgUrl = imgUrl;
    }
}
