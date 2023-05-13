package com.matdori.matdori.domain;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class Store {

    @Id @GeneratedValue
    @Column(name = "store_index")
    private Long id;
    private String name;
    private String category;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String address;
    private String comment;
    @Embedded
    private OpenHours openHours;
    private String img_url;
    private String created_at;
}
