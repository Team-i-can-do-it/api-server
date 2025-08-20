package com.icando.randomBox.entity;

import com.icando.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RandomBox extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "random_box_id")
    private Long id;

    @Column(name = "radom_box_item")
    private String item;

    @Column(name = "radom_box_quantity")
    private int quantity;

    @Column(name = "radom_box_probability")
    private float probability;
}
