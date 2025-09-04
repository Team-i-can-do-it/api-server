package com.icando.member.repository;

import com.icando.member.entity.Member;
import com.icando.member.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member , Long> {

    Optional<Member> findByEmail(String email);
}
