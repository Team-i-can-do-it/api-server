package com.icando.randomBox.repository;

import com.icando.randomBox.entity.RandomBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RandomBoxRepository extends JpaRepository<RandomBox, Long> {
}
