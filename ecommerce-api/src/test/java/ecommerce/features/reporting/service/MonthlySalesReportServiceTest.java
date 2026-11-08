package ecommerce.features.reporting.service;

import ecommerce.features.reporting.dto.ComparisonState;
import ecommerce.features.reporting.dto.DailySalesPoint;
import ecommerce.features.reporting.dto.MonthlySalesReportResponse;
import ecommerce.features.reporting.repository.DailySalesProjection;
import ecommerce.features.reporting.repository.SalesReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MonthlySalesReportService")
class MonthlySalesReportServiceTest {

    private static final int YEAR = 2026;
    private static final int MONTH = 11;

    @Mock
    private SalesReportRepository salesReportRepository;

    @InjectMocks
    private MonthlySalesReportService service;

    private LocalDateTime currentStart;
    private LocalDateTime currentEnd;
    private LocalDateTime previousStart;
    private LocalDateTime previousEnd;

    @BeforeEach
    void setUp() {
        YearMonth current = YearMonth.of(YEAR, MONTH);
        YearMonth previous = current.minusMonths(1);

        currentStart = current.atDay(1).atStartOfDay();
        currentEnd = current.plusMonths(1).atDay(1).atStartOfDay();
        previousStart = previous.atDay(1).atStartOfDay();
        previousEnd = currentStart;
    }

    @Nested
    @DisplayName("camino feliz")
    class HappyPath {

        @BeforeEach
        void stub() {
            when(salesReportRepository.sumApprovedRevenue(eq(currentStart), eq(currentEnd)))
                    .thenReturn(new BigDecimal("1000.00"));
            when(salesReportRepository.sumApprovedRevenue(eq(previousStart), eq(previousEnd)))
                    .thenReturn(new BigDecimal("500.00"));
            when(salesReportRepository.countApprovedOrders(eq(currentStart), eq(currentEnd))).thenReturn(4L);
            when(salesReportRepository.countApprovedOrders(eq(previousStart), eq(previousEnd))).thenReturn(2L);
            when(salesReportRepository.countInitiatedOrders(eq(currentStart), eq(currentEnd))).thenReturn(5L);
            when(salesReportRepository.countInitiatedOrders(eq(previousStart), eq(previousEnd))).thenReturn(4L);
            when(salesReportRepository.sumApprovedRevenueByDay(eq(currentStart), eq(currentEnd)))
                    .thenReturn(List.of(
                            new DailySalesRow(LocalDate.of(2026, 11, 1), new BigDecimal("400.00")),
                            new DailySalesRow(LocalDate.of(2026, 11, 2), new BigDecimal("600.00"))
                    ));
        }

        @Test
        @DisplayName("calcula los 4 KPIs y sus comparativos contra el mes anterior")
        void returnsKpisWithComparison() {
            MonthlySalesReportResponse response = service.generate(YEAR, MONTH);

            assertThat(response.year()).isEqualTo(YEAR);
            assertThat(response.month()).isEqualTo(MONTH);
            assertThat(response.sinDatos()).isFalse();
            assertThat(response.mensaje()).isNull();

            assertThat(response.ingresosTotales().valorActual()).isEqualByComparingTo("1000.00");
            assertThat(response.ingresosTotales().valorAnterior()).isEqualByComparingTo("500.00");
            assertThat(response.ingresosTotales().variacionAbsoluta()).isEqualByComparingTo("500.00");
            assertThat(response.ingresosTotales().variacionPorcentual()).isEqualByComparingTo("100.00");
            assertThat(response.ingresosTotales().estadoComparacion()).isEqualTo(ComparisonState.NORMAL);

            assertThat(response.ordenesExitosas().valorActual()).isEqualByComparingTo("4");
            assertThat(response.ordenesExitosas().variacionPorcentual()).isEqualByComparingTo("100.00");

            assertThat(response.ticketPromedio().valorActual()).isEqualByComparingTo("250.00");
            assertThat(response.ticketPromedio().valorAnterior()).isEqualByComparingTo("250.00");
            assertThat(response.ticketPromedio().variacionPorcentual()).isEqualByComparingTo("0.00");

            assertThat(response.tasaAprobacionPagos().valorActual()).isEqualByComparingTo("80.00");
            assertThat(response.tasaAprobacionPagos().valorAnterior()).isEqualByComparingTo("50.00");
            assertThat(response.tasaAprobacionPagos().variacionPorcentual()).isEqualByComparingTo("60.00");
        }

        @Test
        @DisplayName("la serie diaria cubre todo el mes y su suma coincide con ingresosTotales")
        void dailySeriesSumsToTotalRevenue() {
            MonthlySalesReportResponse response = service.generate(YEAR, MONTH);

            assertThat(response.tendenciaVentasDiarias()).hasSize(30);

            BigDecimal sum = response.tendenciaVentasDiarias().stream()
                    .map(DailySalesPoint::ingresos)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            assertThat(sum).isEqualByComparingTo(response.ingresosTotales().valorActual());
        }
    }

    @Nested
    @DisplayName("mes sin datos")
    class MonthWithoutData {

        @BeforeEach
        void stub() {
            when(salesReportRepository.sumApprovedRevenue(eq(currentStart), eq(currentEnd)))
                    .thenReturn(BigDecimal.ZERO);
            when(salesReportRepository.sumApprovedRevenue(eq(previousStart), eq(previousEnd)))
                    .thenReturn(new BigDecimal("500.00"));
            when(salesReportRepository.countApprovedOrders(eq(currentStart), eq(currentEnd))).thenReturn(0L);
            when(salesReportRepository.countApprovedOrders(eq(previousStart), eq(previousEnd))).thenReturn(2L);
            when(salesReportRepository.countInitiatedOrders(eq(currentStart), eq(currentEnd))).thenReturn(5L);
            when(salesReportRepository.countInitiatedOrders(eq(previousStart), eq(previousEnd))).thenReturn(4L);
            when(salesReportRepository.sumApprovedRevenueByDay(eq(currentStart), eq(currentEnd)))
                    .thenReturn(List.of());
        }

        @Test
        @DisplayName("activa sinDatos, setea el mensaje y devuelve la serie en cero")
        void returnsEmptyReport() {
            MonthlySalesReportResponse response = service.generate(YEAR, MONTH);

            assertThat(response.sinDatos()).isTrue();
            assertThat(response.mensaje()).isEqualTo("No se registraron ventas aprobadas en el período seleccionado");

            assertThat(response.ingresosTotales().valorActual()).isEqualByComparingTo("0.00");
            assertThat(response.ordenesExitosas().valorActual()).isEqualByComparingTo("0");
            assertThat(response.ticketPromedio().valorActual()).isEqualByComparingTo("0.00");
            assertThat(response.tasaAprobacionPagos().valorActual()).isEqualByComparingTo("0.00");

            assertThat(response.tendenciaVentasDiarias()).hasSize(30);
            assertThat(response.tendenciaVentasDiarias())
                    .allMatch(point -> point.ingresos().compareTo(BigDecimal.ZERO) == 0);
        }
    }

    @Nested
    @DisplayName("mes anterior en cero (división por cero)")
    class PreviousMonthZero {

        @BeforeEach
        void stub() {
            when(salesReportRepository.sumApprovedRevenue(eq(currentStart), eq(currentEnd)))
                    .thenReturn(new BigDecimal("1000.00"));
            when(salesReportRepository.sumApprovedRevenue(eq(previousStart), eq(previousEnd)))
                    .thenReturn(BigDecimal.ZERO);
            when(salesReportRepository.countApprovedOrders(eq(currentStart), eq(currentEnd))).thenReturn(4L);
            when(salesReportRepository.countApprovedOrders(eq(previousStart), eq(previousEnd))).thenReturn(0L);
            when(salesReportRepository.countInitiatedOrders(eq(currentStart), eq(currentEnd))).thenReturn(5L);
            when(salesReportRepository.countInitiatedOrders(eq(previousStart), eq(previousEnd))).thenReturn(0L);
            when(salesReportRepository.sumApprovedRevenueByDay(eq(currentStart), eq(currentEnd)))
                    .thenReturn(List.of(
                            new DailySalesRow(LocalDate.of(2026, 11, 1), new BigDecimal("1000.00"))
                    ));
        }

        @Test
        @DisplayName("marca SIN_COMPARATIVO con variacionPorcentual nula")
        void returnsSinComparativo() {
            MonthlySalesReportResponse response = service.generate(YEAR, MONTH);

            assertThat(response.ingresosTotales().estadoComparacion()).isEqualTo(ComparisonState.SIN_COMPARATIVO);
            assertThat(response.ingresosTotales().variacionPorcentual()).isNull();
            assertThat(response.ingresosTotales().variacionAbsoluta()).isEqualByComparingTo("1000.00");
        }
    }

    @Nested
    @DisplayName("mes actual en cero con mes anterior positivo")
    class CurrentMonthZero {

        @BeforeEach
        void stub() {
            when(salesReportRepository.sumApprovedRevenue(eq(currentStart), eq(currentEnd)))
                    .thenReturn(BigDecimal.ZERO);
            when(salesReportRepository.sumApprovedRevenue(eq(previousStart), eq(previousEnd)))
                    .thenReturn(new BigDecimal("500.00"));
            when(salesReportRepository.countApprovedOrders(eq(currentStart), eq(currentEnd))).thenReturn(0L);
            when(salesReportRepository.countApprovedOrders(eq(previousStart), eq(previousEnd))).thenReturn(2L);
            when(salesReportRepository.countInitiatedOrders(eq(currentStart), eq(currentEnd))).thenReturn(0L);
            when(salesReportRepository.countInitiatedOrders(eq(previousStart), eq(previousEnd))).thenReturn(4L);
            when(salesReportRepository.sumApprovedRevenueByDay(eq(currentStart), eq(currentEnd)))
                    .thenReturn(List.of());
        }

        @Test
        @DisplayName("calcula -100% con estado NORMAL")
        void returnsMinus100Percent() {
            MonthlySalesReportResponse response = service.generate(YEAR, MONTH);

            assertThat(response.ingresosTotales().estadoComparacion()).isEqualTo(ComparisonState.NORMAL);
            assertThat(response.ingresosTotales().variacionPorcentual()).isEqualByComparingTo("-100.00");
        }
    }

    private static final class DailySalesRow implements DailySalesProjection {
        private final LocalDate day;
        private final BigDecimal total;

        DailySalesRow(LocalDate day, BigDecimal total) {
            this.day = day;
            this.total = total;
        }

        @Override
        public LocalDate getDay() {
            return day;
        }

        @Override
        public BigDecimal getTotal() {
            return total;
        }
    }
}
