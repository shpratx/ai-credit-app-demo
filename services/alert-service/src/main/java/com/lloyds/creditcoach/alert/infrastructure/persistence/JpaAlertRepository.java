package com.lloyds.creditcoach.alert.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaAlertRepository extends JpaRepository<AlertEntity, UUID> {

    @Query("SELECT a FROM AlertEntity a WHERE a.customerId = :customerId AND a.isDeleted = false ORDER BY CASE a.status WHEN 'UNREAD' THEN 0 WHEN 'READ' THEN 1 ELSE 2 END, a.createdAt DESC")
    Page<AlertEntity> findByCustomerIdOrdered(UUID customerId, Pageable pageable);
}
