package com.matdori.matdori.domain;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Store {

    @Id @GeneratedValue
    @Column(name = "store_index")
    private Long id;
    private String name;
    private String category;
    private String phone_number;
    private String address;
    private String comment;
    private String img_url;
    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String saturday;
    private String sunday;
    private String created_at;
}
