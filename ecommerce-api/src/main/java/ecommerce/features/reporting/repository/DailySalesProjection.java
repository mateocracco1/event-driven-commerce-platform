package ecommerce.features.reporting.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailySalesProjection {

    LocalDate getDay();

    BigDecimal getTotal();
}
