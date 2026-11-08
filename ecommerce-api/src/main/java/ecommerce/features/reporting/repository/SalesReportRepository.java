package ecommerce.features.reporting.repository;

import ecommerce.features.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesReportRepository extends JpaRepository<Order, Long> {

    @Query("""
            SELECT SUM(o.totalAmount)
            FROM Order o
            WHERE o.createdAt >= :start
              AND o.createdAt < :end
              AND (
                EXISTS (
                  SELECT 1 FROM Payment p
                  WHERE p.order = o
                    AND p.status = ecommerce.features.payment.domain.PaymentStatus.APPROVED
                )
                OR (
                  NOT EXISTS (SELECT 1 FROM Payment p WHERE p.order = o)
                  AND o.status IN (
                    ecommerce.features.order.domain.OrderStatus.PAID,
                    ecommerce.features.order.domain.OrderStatus.PROCESSING,
                    ecommerce.features.order.domain.OrderStatus.DELIVERED
                  )
                )
              )
            """)
    BigDecimal sumApprovedRevenue(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
            SELECT COUNT(o)
            FROM Order o
            WHERE o.createdAt >= :start
              AND o.createdAt < :end
              AND (
                EXISTS (
                  SELECT 1 FROM Payment p
                  WHERE p.order = o
                    AND p.status = ecommerce.features.payment.domain.PaymentStatus.APPROVED
                )
                OR (
                  NOT EXISTS (SELECT 1 FROM Payment p WHERE p.order = o)
                  AND o.status IN (
                    ecommerce.features.order.domain.OrderStatus.PAID,
                    ecommerce.features.order.domain.OrderStatus.PROCESSING,
                    ecommerce.features.order.domain.OrderStatus.DELIVERED
                  )
                )
              )
            """)
    long countApprovedOrders(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
            SELECT COUNT(o)
            FROM Order o
            WHERE o.createdAt >= :start
              AND o.createdAt < :end
            """)
    long countInitiatedOrders(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
            SELECT FUNCTION('DATE', o.createdAt) AS day, SUM(o.totalAmount) AS total
            FROM Order o
            WHERE o.createdAt >= :start
              AND o.createdAt < :end
              AND (
                EXISTS (
                  SELECT 1 FROM Payment p
                  WHERE p.order = o
                    AND p.status = ecommerce.features.payment.domain.PaymentStatus.APPROVED
                )
                OR (
                  NOT EXISTS (SELECT 1 FROM Payment p WHERE p.order = o)
                  AND o.status IN (
                    ecommerce.features.order.domain.OrderStatus.PAID,
                    ecommerce.features.order.domain.OrderStatus.PROCESSING,
                    ecommerce.features.order.domain.OrderStatus.DELIVERED
                  )
                )
              )
            GROUP BY FUNCTION('DATE', o.createdAt)
            ORDER BY FUNCTION('DATE', o.createdAt)
            """)
    List<DailySalesProjection> sumApprovedRevenueByDay(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
