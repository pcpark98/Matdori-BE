package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class User {

    @Id @GeneratedValue
    @Column(name = "user_index")
    private Long id;

    private String email;
    private String password;
    private String nickname;
    private String department;
    @Column(name = "created_at")
    private String createdAt;


}
