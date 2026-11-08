package ecommerce.features.order;

import ecommerce.core.domain.User;
import ecommerce.features.order.domain.Order;
import ecommerce.features.order.domain.OrderStatus;
import ecommerce.features.payment.domain.Payment;
import ecommerce.features.payment.domain.PaymentStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public class OrderSpecification {

    private OrderSpecification() {
    }

    public static Specification<Order> build(String search, OrderStatus status, String paymentStatus,
                                             LocalDate startDate, LocalDate endDate) {
        return Specification.allOf(
                hasSearch(search),
                hasStatus(status),
                hasPaymentStatus(paymentStatus),
                createdAtBetween(startDate, endDate)
        );
    }

    private static Specification<Order> hasSearch(String search) {
        if (!StringUtils.hasText(search)) {
            return null;
        }

        String trimmed = search.trim();
        String pattern = "%" + trimmed.toLowerCase() + "%";

        Long numericId = parseLongOrNull(trimmed);

        return (root, query, cb) -> {
            Join<Order, User> customer = root.join("customer", JoinType.LEFT);

            Predicate predicate = cb.or(
                    cb.like(cb.lower(customer.get("name")), pattern),
                    cb.like(cb.lower(customer.get("lastName")), pattern),
                    cb.like(cb.lower(customer.get("email")), pattern),
                    cb.like(cb.lower(root.get("orderNumber")), pattern)
            );

            if (numericId != null) {
                predicate = cb.or(predicate, cb.equal(root.get("id"), numericId));
            }

            return predicate;
        };
    }

    private static Specification<Order> hasStatus(OrderStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    private static Specification<Order> hasPaymentStatus(String paymentStatus) {
        if (!StringUtils.hasText(paymentStatus)) {
            return null;
        }

        return switch (paymentStatus.trim().toUpperCase()) {
            case "APPROVED" -> (root, query, cb) -> cb.or(
                    paymentExistsWithStatus(query, cb, root, PaymentStatus.APPROVED),
                    cb.and(
                            cb.not(paymentExistsWithStatus(query, cb, root)),
                            root.get("status").in(OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.DELIVERED)
                    )
            );
            case "PENDING" -> (root, query, cb) -> cb.or(
                    paymentExistsWithStatus(query, cb, root, PaymentStatus.PENDING),
                    cb.and(
                            cb.not(paymentExistsWithStatus(query, cb, root)),
                            cb.equal(root.get("status"), OrderStatus.PENDING_PAYMENT)
                    )
            );
            case "REJECTED" -> (root, query, cb) -> cb.or(
                    paymentExistsWithStatus(query, cb, root, PaymentStatus.REJECTED, PaymentStatus.REFUNDED),
                    cb.and(
                            cb.not(paymentExistsWithStatus(query, cb, root)),
                            cb.equal(root.get("status"), OrderStatus.CANCELLED)
                    )
            );
            default -> null;
        };
    }

    private static Specification<Order> createdAtBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (startDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdAt"), startDate.atStartOfDay()));
            }
            if (endDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createdAt"), endDate.atTime(23, 59, 59)));
            }
            return predicate;
        };
    }

    private static Predicate paymentExistsWithStatus(CriteriaQuery<?> query,
                                                     CriteriaBuilder cb,
                                                     Root<Order> root,
                                                      PaymentStatus... statuses) {
        Subquery<Long> sub = query.subquery(Long.class);
        Root<Payment> payment = sub.from(Payment.class);
        sub.select(payment.get("id"));

        Predicate predicate = cb.equal(payment.get("order").get("id"), root.get("id"));
        if (statuses != null && statuses.length > 0) {
            predicate = cb.and(predicate, payment.get("status").in((Object[]) statuses));
        }
        sub.where(predicate);

        return cb.exists(sub);
    }

    private static Long parseLongOrNull(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
