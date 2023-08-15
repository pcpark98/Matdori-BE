package com.matdori.matdori.repositoy.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JokboRichStore {
    private Long storeIndex;
    private String name;
    private String imgUrl;
    private Long jokboCnt;

    public JokboRichStore(Long storeIndex, String name, String imgUrl, Long jokboCnt) {
        this.storeIndex = storeIndex;
        this.name = name;
        this.imgUrl = imgUrl;
        this.jokboCnt = jokboCnt;
    }
}
