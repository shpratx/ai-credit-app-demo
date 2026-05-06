package com.lloyds.creditcoach.creditscore.infrastructure.persistence;

import com.lloyds.creditcoach.creditscore.domain.model.CraApiAuditLog;
import com.lloyds.creditcoach.creditscore.domain.port.AuditLogRepository;
import org.springframework.stereotype.Component;

@Component
public class AuditLogRepositoryAdapter implements AuditLogRepository {

    private final JpaAuditLogRepository jpaRepository;

    public AuditLogRepositoryAdapter(JpaAuditLogRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CraApiAuditLog save(CraApiAuditLog auditLog) {
        var entity = new CraApiAuditLogEntity();
        entity.setId(auditLog.getId());
        entity.setCustomerId(auditLog.getCustomerId());
        entity.setProvider(auditLog.getProvider());
        entity.setRequestHash(auditLog.getRequestHash());
        entity.setResponseStatus(auditLog.getResponseStatus());
        entity.setLatencyMs(auditLog.getLatencyMs());
        entity.setCircuitBreakerState(auditLog.getCircuitBreakerState());
        entity.setCorrelationId(auditLog.getCorrelationId());
        entity.setCreatedAt(auditLog.getCreatedAt());
        jpaRepository.save(entity);
        return auditLog;
    }
}
