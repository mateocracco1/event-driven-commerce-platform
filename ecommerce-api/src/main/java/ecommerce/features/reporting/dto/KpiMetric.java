package ecommerce.features.reporting.dto;

import java.math.BigDecimal;

public record KpiMetric(
        BigDecimal valorActual,
        BigDecimal valorAnterior,
        BigDecimal variacionAbsoluta,
        BigDecimal variacionPorcentual,
        ComparisonState estadoComparacion
) {}
