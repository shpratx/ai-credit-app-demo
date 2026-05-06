package com.lloyds.creditcoach.compliance.domain.port;

import com.lloyds.creditcoach.compliance.domain.model.DsarExport;

import java.util.Optional;
import java.util.UUID;

public interface DsarExportRepository {
    DsarExport save(DsarExport export);
    Optional<DsarExport> findByCustomerId(UUID customerId);
}
