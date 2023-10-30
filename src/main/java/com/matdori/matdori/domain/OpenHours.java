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
    @Column(length = 60)
    private String monday;
    @Column(length = 60)
    private String tuesday;
    @Column(length = 60)
    private String wednesday;
    @Column(length = 60)
    private String thursday;
    @Column(length = 60)
    private String friday;
    @Column(length = 60)
    private String saturday;
    @Column(length = 60)
    private String sunday;
}
