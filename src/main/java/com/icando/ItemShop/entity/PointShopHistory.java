package com.icando.ItemShop.entity;

import com.icando.global.BaseEntity;
import com.icando.member.entity.Member;
import com.icando.member.entity.PointHistory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointShopHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "random_box_history_id")
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id" , nullable = false)
    private Member member;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name ="item_id" , nullable = false)
    private Item item;

    @Column(name = "item_buier_number")
    private String number;

    private PointShopHistory (Member member,Item item, String number) {
        this.member = member;
        this.item = item;
        this.number = number;
    }

    public static PointShopHistory byPhoneNumber(Member member,Item item,String phoneNumber) {
        return new PointShopHistory(member,item,phoneNumber);
    }
}
