package com.icando.ItemShop.repository;

import com.icando.ItemShop.entity.PointShopHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointShopHistoryRepository extends JpaRepository<PointShopHistory,Long> {

    List<PointShopHistory> findTop10ByMemberIdOrderByCreatedAtDesc(Long memberId);

    Page<PointShopHistory> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
