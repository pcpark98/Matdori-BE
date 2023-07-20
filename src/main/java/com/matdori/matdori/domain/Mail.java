package com.matdori.matdori.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Mail {
    private String address;
    private String title;
    private String message;
}
