package com.icando.member.entity;

import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long id;

    @Column(name = "member_point")
    @ColumnDefault("0")
    private int point;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id" , nullable = false)
    private Member member;

    private Point (int point ,Member member){
        this.point = point;
        this.member = member;
    }

    public static Point of(int point, Member member){
        return new Point(point,member);
    }

    public void decreasePoint(int itemPoint){
        if(point < itemPoint){
            throw new MemberException(MemberErrorCode.NOT_ENOUGH_POINTS);}
        point -= itemPoint;
    }

    public void earnPoints(int getPoint) {
        point += getPoint;
    }

}
