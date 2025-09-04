package com.icando.member.repository;

import com.icando.member.entity.Mbti;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MbtiRepository extends JpaRepository<Mbti, Long> {
    Optional<Mbti> findByName(String name);
}
