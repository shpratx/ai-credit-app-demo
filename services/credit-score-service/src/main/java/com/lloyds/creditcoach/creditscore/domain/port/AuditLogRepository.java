package com.lloyds.creditcoach.creditscore.domain.port;

import com.lloyds.creditcoach.creditscore.domain.model.CraApiAuditLog;

public interface AuditLogRepository {

    CraApiAuditLog save(CraApiAuditLog auditLog);
}
