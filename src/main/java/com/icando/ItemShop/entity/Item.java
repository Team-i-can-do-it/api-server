package com.icando.ItemShop.entity;

import com.icando.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "item_name",nullable = false)
    private String name;

    @Column(name = "item_image_url",nullable = false)
    private String imageUrl;

    @Column(name = "item_quantity",nullable = false)
    private int quantity;

    @Column(name = "item_point",nullable = false)
    private int point;

    private Item(String name,String imageUrl, int quantity, int point){
        this.name = name;
        this.imageUrl = imageUrl;
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

    public static Item of (String name,String imageUrl, int quantity, int point) {
        return new Item(name,imageUrl, quantity,point);
    }
}
