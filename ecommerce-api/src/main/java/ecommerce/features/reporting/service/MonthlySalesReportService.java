package ecommerce.features.reporting.service;

import ecommerce.features.reporting.dto.ComparisonState;
import ecommerce.features.reporting.dto.DailySalesPoint;
import ecommerce.features.reporting.dto.KpiMetric;
import ecommerce.features.reporting.dto.MonthlySalesReportResponse;
import ecommerce.features.reporting.repository.DailySalesProjection;
import ecommerce.features.reporting.repository.SalesReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonthlySalesReportService {

    private static final String SIN_DATOS_MESSAGE = "No se registraron ventas aprobadas en el período seleccionado";

    private final SalesReportRepository salesReportRepository;

    @Transactional(readOnly = true)
    public MonthlySalesReportResponse generate(int year, int month) {
        YearMonth current = YearMonth.of(year, month);
        YearMonth previous = current.minusMonths(1);

        LocalDateTime currentStart = current.atDay(1).atStartOfDay();
        LocalDateTime currentEnd = current.plusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime previousStart = previous.atDay(1).atStartOfDay();
        LocalDateTime previousEnd = currentStart;

        BigDecimal currentRevenue = nullSafe(salesReportRepository.sumApprovedRevenue(currentStart, currentEnd));
        long currentOrders = salesReportRepository.countApprovedOrders(currentStart, currentEnd);
        long currentInitiated = salesReportRepository.countInitiatedOrders(currentStart, currentEnd);

        BigDecimal previousRevenue = nullSafe(salesReportRepository.sumApprovedRevenue(previousStart, previousEnd));
        long previousOrders = salesReportRepository.countApprovedOrders(previousStart, previousEnd);
        long previousInitiated = salesReportRepository.countInitiatedOrders(previousStart, previousEnd);

        BigDecimal currentTicket = currentOrders == 0
                ? BigDecimal.ZERO
                : currentRevenue.divide(BigDecimal.valueOf(currentOrders), 2, RoundingMode.HALF_UP);
        BigDecimal previousTicket = previousOrders == 0
                ? BigDecimal.ZERO
                : previousRevenue.divide(BigDecimal.valueOf(previousOrders), 2, RoundingMode.HALF_UP);

        BigDecimal currentApprovalRate = currentInitiated == 0
                ? BigDecimal.ZERO
                : percentage(currentOrders, currentInitiated);
        BigDecimal previousApprovalRate = previousInitiated == 0
                ? BigDecimal.ZERO
                : percentage(previousOrders, previousInitiated);

        boolean sinDatos = currentOrders == 0;
        String mensaje = sinDatos ? SIN_DATOS_MESSAGE : null;

        KpiMetric ingresosTotales = buildKpi(currentRevenue, previousRevenue);
        KpiMetric ordenesExitosas = buildKpi(BigDecimal.valueOf(currentOrders), BigDecimal.valueOf(previousOrders));
        KpiMetric ticketPromedio = buildKpi(currentTicket, previousTicket);
        KpiMetric tasaAprobacionPagos = buildKpi(currentApprovalRate, previousApprovalRate);

        List<DailySalesPoint> tendencia = buildDailyTrend(current, currentStart, currentEnd);

        return new MonthlySalesReportResponse(
                year,
                month,
                Instant.now(),
                sinDatos,
                mensaje,
                ingresosTotales,
                ordenesExitosas,
                ticketPromedio,
                tasaAprobacionPagos,
                tendencia
        );
    }

    private KpiMetric buildKpi(BigDecimal actual, BigDecimal previous) {
        BigDecimal variacionAbsoluta = actual.subtract(previous);
        BigDecimal variacionPorcentual;
        ComparisonState estado;

        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            if (actual.compareTo(BigDecimal.ZERO) == 0) {
                variacionPorcentual = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                estado = ComparisonState.NORMAL;
            } else {
                variacionPorcentual = null;
                estado = ComparisonState.SIN_COMPARATIVO;
            }
        } else {
            variacionPorcentual = actual.subtract(previous)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(previous, 2, RoundingMode.HALF_UP);
            estado = ComparisonState.NORMAL;
        }

        return new KpiMetric(actual, previous, variacionAbsoluta, variacionPorcentual, estado);
    }

    private List<DailySalesPoint> buildDailyTrend(YearMonth month, LocalDateTime start, LocalDateTime end) {
        Map<LocalDate, BigDecimal> byDay = salesReportRepository.sumApprovedRevenueByDay(start, end).stream()
                .collect(Collectors.toMap(DailySalesProjection::getDay, DailySalesProjection::getTotal));

        List<DailySalesPoint> tendencia = new ArrayList<>(month.lengthOfMonth());
        for (int day = 1; day <= month.lengthOfMonth(); day++) {
            LocalDate fecha = month.atDay(day);
            BigDecimal ingresos = byDay.getOrDefault(fecha, BigDecimal.ZERO);
            tendencia.add(new DailySalesPoint(fecha, ingresos));
        }
        return tendencia;
    }

    private BigDecimal percentage(long approved, long initiated) {
        return BigDecimal.valueOf(approved)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(initiated), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
