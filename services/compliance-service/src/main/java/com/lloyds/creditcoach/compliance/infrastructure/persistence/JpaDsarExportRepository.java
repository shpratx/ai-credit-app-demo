package com.lloyds.creditcoach.compliance.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaDsarExportRepository extends JpaRepository<DsarExportEntity, UUID> {
    Optional<DsarExportEntity> findByCustomerId(UUID customerId);
}
