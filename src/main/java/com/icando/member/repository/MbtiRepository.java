package com.icando.member.repository;

import com.icando.member.entity.Mbti;
import com.icando.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MbtiRepository extends JpaRepository<Mbti,Long> {
    Optional<Mbti> findFirstByMemberIdOrderByModifiedAtDesc(Long memberId);
    Optional<Mbti> findByName(String name);
    Boolean existsByMemberAndName(Member member, String name);
}
