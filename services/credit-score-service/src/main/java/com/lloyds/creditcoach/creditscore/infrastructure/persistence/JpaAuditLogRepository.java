package com.lloyds.creditcoach.creditscore.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaAuditLogRepository extends JpaRepository<CraApiAuditLogEntity, UUID> {
}
