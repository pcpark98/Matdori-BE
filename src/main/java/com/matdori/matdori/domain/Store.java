package com.matdori.matdori.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
public class Store {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_index")
    private Long id;

    @NotBlank
    @Column(length =30)
    @Size(max = 30)
    private String name;

    @NotBlank
    @Column(length =20, unique = true)
    @Size(max = 20)
    @Enumerated(EnumType.STRING)
    private StoreCategory category;


    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(length = 100)
    private String address;

    @Embedded
    private OpenHours openHours;

    private String imgUrl;

    @CreationTimestamp
    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "store")
    private List<Jokbo> jokbos;
}
