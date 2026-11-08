package ecommerce.features.reporting.dto;

import java.time.Instant;
import java.util.List;

public record MonthlySalesReportResponse(
        Integer year,
        Integer month,
        Instant generatedAt,
        boolean sinDatos,
        String mensaje,
        KpiMetric ingresosTotales,
        KpiMetric ordenesExitosas,
        KpiMetric ticketPromedio,
        KpiMetric tasaAprobacionPagos,
        List<DailySalesPoint> tendenciaVentasDiarias
) {}
