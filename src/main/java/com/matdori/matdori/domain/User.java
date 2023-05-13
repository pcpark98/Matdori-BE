package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

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
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
