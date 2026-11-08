package ecommerce.features.reporting.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailySalesPoint(
        LocalDate fecha,
        BigDecimal ingresos
) {}
