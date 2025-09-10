package com.icando.member.entity;

import com.icando.global.BaseEntity;
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
public class PointHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_history_id")
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id" , nullable = false)
    private Member member;

    @Column(name = "points")
    private int points;

    @Enumerated
    @Column(name = "point_activity_type")
    private ActivityType activityType;

    private PointHistory(Member member,int points,ActivityType activityType){
        this.points = points;
        this.member = member;
        this.activityType = activityType;
    }

    public static PointHistory of(Member member, int points, ActivityType activityType){
        return new PointHistory(member,points, activityType);
    }


}
