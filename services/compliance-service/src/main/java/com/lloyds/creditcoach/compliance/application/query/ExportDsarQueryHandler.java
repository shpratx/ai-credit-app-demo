package com.lloyds.creditcoach.compliance.application.query;

import com.lloyds.creditcoach.compliance.application.dto.DsarExportResponse;
import com.lloyds.creditcoach.compliance.domain.model.DsarExport;
import com.lloyds.creditcoach.compliance.domain.port.DsarExportRepository;
import com.lloyds.creditcoach.compliance.infrastructure.client.ServiceAggregatorClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class ExportDsarQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(ExportDsarQueryHandler.class);
    private final DsarExportRepository dsarExportRepository;
    private final ServiceAggregatorClient serviceAggregatorClient;

    public ExportDsarQueryHandler(DsarExportRepository dsarExportRepository,
                                  ServiceAggregatorClient serviceAggregatorClient) {
        this.dsarExportRepository = dsarExportRepository;
        this.serviceAggregatorClient = serviceAggregatorClient;
    }

    @Transactional
    public DsarExportResponse handle(UUID customerId) {
        log.info("Processing DSAR export for customer: {}", customerId);

        var export = new DsarExport();
        export.setCustomerId(customerId);
        export.setStatus(DsarExport.DsarStatus.PROCESSING);
        dsarExportRepository.save(export);

        // Aggregate data from all services (GDPR Art. 15)
        Map<String, Object> aggregatedData = serviceAggregatorClient.aggregateAllData(customerId);

        String downloadUrl = "/api/v1/credit-coach/compliance/" + customerId + "/dsar/download/" + export.getId();
        export.markReady(downloadUrl);
        var saved = dsarExportRepository.save(export);

        log.info("DSAR export ready for customer: {}", customerId);
        return new DsarExportResponse(saved.getId(), saved.getCustomerId(),
                saved.getStatus().name(), saved.getRequestedAt(), saved.getCompletedAt(), saved.getDownloadUrl());
    }
}
