package com.icando.randomBox.entity;

import com.icando.global.BaseEntity;
import com.icando.randomBox.exception.RandomBoxErrorCode;
import com.icando.randomBox.exception.RandomBoxException;
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

    private RandomBox(String item,int quantity){
        this.item = item;
        this.quantity = quantity;
    }

    private RandomBox(String item, int quantity, float probability){
        this.item = item;
        this.quantity = quantity;
        this.probability = probability;
    }

    //TODO:추후 가챠 뽑기 구현 시 사용 예정
//    public void decreaseQuantity(int count) {
//        if (quantity == 0){
//            throw new RandomBoxException(RandomBoxErrorCode.OUT_OF_STOCK);
//        }
//        quantity -= count;
//    }

    public static RandomBox of (String item,int quantity, float probability) {
        return new RandomBox(item,quantity,probability);
    }
}
