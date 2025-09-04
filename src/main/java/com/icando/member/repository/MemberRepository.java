package com.icando.member.repository;

import com.icando.member.dto.MbtiDtos;
import com.icando.member.dto.MbtiRecentlyDto;
import com.icando.member.dto.SearchMypageDto;
import com.icando.member.entity.Mbti;
import com.icando.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member , Long> {

    Optional<Member> findByEmail(String email);
//    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);

    @Query("SELECT new com.icando.member.dto.SearchMypageDto(m.name, p.point) " +
            "FROM Point p " +
            "JOIN p.member m " +
            "WHERE m.email = :email")
    Optional<SearchMypageDto> findMyPageByEmail(@Param("email") String email);

    @Query("SELECT new com.icando.member.dto.MbtiDtos(mb.name, mb.id)" +
            "FROM Mbti mb " +
            "JOIN mb.member m " +
            "Where m.email = :email")
    List<MbtiDtos> findAllMbtiDto(@Param("email") String email);

    @Query("SELECT new com.icando.member.dto.MbtiRecentlyDto(mb.name, mb.id) " +
            "FROM Mbti mb " +
            "JOIN mb.member m " +
            "WHERE m.email = :email " +
            "ORDER BY mb.modifiedAt DESC")
    List<MbtiRecentlyDto> findRecentlyMbti(@Param("email") String email, Pageable pageable);

}
