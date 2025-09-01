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

    //TODO: 추후 가챠 뽑기 로직 작성 시 사용 예정
//    public void decreasePoint(int gamePoint){
//        if(point < gamePoint){
//            throw new MemberException(MemberErrorCode.NOT_ENOUGH_POINTS);}
//        point -= gamePoint;
//    }

}
