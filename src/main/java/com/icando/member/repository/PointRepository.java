package com.icando.member.repository;

import com.icando.member.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<PointHistory,Long> {

    Optional<Point> findPointByMemberId(Long memberId);
    Optional<PointHistory> findPointByMemberId(Long memberId);
}
