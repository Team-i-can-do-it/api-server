package com.icando.member.repository;

import com.icando.member.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<Point,Long> {

//    Optional<Point> findByMemberId(Long memberId);
}
