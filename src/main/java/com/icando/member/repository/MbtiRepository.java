package com.icando.member.repository;

import com.icando.member.entity.Mbti;
import com.icando.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MbtiRepository extends JpaRepository<Mbti,Long> {
    Optional<Mbti> findFirstByMemberIdOrderByModifiedAtDesc(Long memberId);
    Optional<Mbti> findByName(String name);
    List<Mbti> findAllByMemberId(Long memberId);
    Boolean existsByMemberAndName(Member member, String name);

    @Modifying
    @Query("DELETE FROM Mbti m WHERE m.member.id = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
