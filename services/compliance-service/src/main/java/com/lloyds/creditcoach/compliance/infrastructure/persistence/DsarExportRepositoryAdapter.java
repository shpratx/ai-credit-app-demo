package com.lloyds.creditcoach.compliance.infrastructure.persistence;

import com.lloyds.creditcoach.compliance.domain.model.DsarExport;
import com.lloyds.creditcoach.compliance.domain.port.DsarExportRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class DsarExportRepositoryAdapter implements DsarExportRepository {

    private final JpaDsarExportRepository jpaRepository;

    public DsarExportRepositoryAdapter(JpaDsarExportRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public DsarExport save(DsarExport export) {
        var entity = toEntity(export);
        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<DsarExport> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerId(customerId).map(this::toDomain);
    }

    private DsarExport toDomain(DsarExportEntity e) {
        var d = new DsarExport();
        d.setId(e.getId());
        d.setCustomerId(e.getCustomerId());
        d.setStatus(DsarExport.DsarStatus.valueOf(e.getStatus()));
        d.setRequestedAt(e.getRequestedAt());
        d.setCompletedAt(e.getCompletedAt());
        d.setDownloadUrl(e.getDownloadUrl());
        return d;
    }

    private DsarExportEntity toEntity(DsarExport d) {
        var e = new DsarExportEntity();
        e.setId(d.getId());
        e.setCustomerId(d.getCustomerId());
        e.setStatus(d.getStatus().name());
        e.setRequestedAt(d.getRequestedAt());
        e.setCompletedAt(d.getCompletedAt());
        e.setDownloadUrl(d.getDownloadUrl());
        return e;
    }
}
