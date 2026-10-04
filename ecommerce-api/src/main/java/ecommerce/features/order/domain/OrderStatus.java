package ecommerce.features.order.domain;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    PROCESSING,
    DELIVERED,
    CANCELLED
}
