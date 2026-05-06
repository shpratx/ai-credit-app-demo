package com.lloyds.creditcoach.compliance.unit;

import com.lloyds.creditcoach.compliance.application.query.ExportDsarQueryHandler;
import com.lloyds.creditcoach.compliance.domain.model.DsarExport;
import com.lloyds.creditcoach.compliance.domain.port.DsarExportRepository;
import com.lloyds.creditcoach.compliance.infrastructure.client.ServiceAggregatorClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExportDsarQueryHandler")
class ExportDsarQueryHandlerTest {

    @Mock private DsarExportRepository dsarExportRepository;
    @Mock private ServiceAggregatorClient serviceAggregatorClient;

    private ExportDsarQueryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ExportDsarQueryHandler(dsarExportRepository, serviceAggregatorClient);
    }

    @Test
    @DisplayName("should aggregate data from all services for DSAR export")
    void should_aggregateAllServiceData_when_dsarRequested() {
        var customerId = UUID.randomUUID();
        when(dsarExportRepository.save(any(DsarExport.class))).thenAnswer(i -> i.getArgument(0));
        when(serviceAggregatorClient.aggregateAllData(customerId)).thenReturn(Map.of("consent", "data", "scores", "data"));

        var result = handler.handle(customerId);

        assertThat(result).isNotNull();
        assertThat(result.customerId()).isEqualTo(customerId);
        assertThat(result.status()).isEqualTo("READY");
        verify(serviceAggregatorClient).aggregateAllData(customerId);
    }
}
