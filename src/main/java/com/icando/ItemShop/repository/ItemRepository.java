package com.icando.ItemShop.repository;

import com.icando.ItemShop.entity.RandomBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RandomBoxRepository extends JpaRepository<RandomBox, Long> {
}
