package com.cobolmodernization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cobolmodernization.entity.SalesRecordEntity;

@Repository
public interface SalesRecordRepository extends JpaRepository<SalesRecordEntity, Long> {

    List<SalesRecordEntity> findByProductCode(String productCode);
}
