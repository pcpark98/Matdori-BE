package com.matdori.matdori.domain;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
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
    private String imgUrl;
    @CreationTimestamp
    private LocalDateTime created_at;

    @OneToMany(mappedBy = "store")
    private List<Jokbo> jokbos;
}
