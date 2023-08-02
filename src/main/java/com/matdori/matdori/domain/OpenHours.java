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
    @Column(length = 30)
    private String monday;
    @Column(length = 30)
    private String tuesday;
    @Column(length = 30)
    private String wednesday;
    @Column(length = 30)
    private String thursday;
    @Column(length = 30)
    private String friday;
    @Column(length = 30)
    private String saturday;
    @Column(length = 30)
    private String sunday;
}
