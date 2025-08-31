package com.icando.ItemShop.entity;

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
    @Column(name = "item_id")
    private Long id;

    @Column(name = "item_name")
    private String name;

    @Column(name = "item_quantity")
    private int quantity;

    @Column(name = "item_point")
    private int point;

    private RandomBox(String name,int quantity, int point){
        this.name = name;
        this.quantity = quantity;
        this.point = point;
    }

    //TODO:추후 아이템 구매 구현 시 사용 예정
//    public void decreaseQuantity(int count) {
//        if (quantity == 0){
//            throw new RandomBoxException(RandomBoxErrorCode.OUT_OF_STOCK);
//        }
//        quantity -= count;
//    }

    public static RandomBox of (String name,int quantity, int point) {
        return new RandomBox(name,quantity,point);
    }
}
