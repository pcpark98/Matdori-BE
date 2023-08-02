package com.matdori.matdori.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
public class OpenHours {
    @Column(length = 20)
    private String monday;
    @Column(length = 20)
    private String tuesday;
    @Column(length = 20)
    private String wednesday;
    @Column(length = 20)
    private String thursday;
    @Column(length = 20)
    private String friday;
    @Column(length = 20)
    private String saturday;
    @Column(length = 20)
    private String sunday;
}
