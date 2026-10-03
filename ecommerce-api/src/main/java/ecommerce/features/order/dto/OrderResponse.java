package ecommerce.features.order.dto;

import ecommerce.features.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        OrderStatus status,
        Long customerId,
        BigDecimal totalAmount,
        List<OrderItemResponse> items,
        LocalDateTime createdAt,
        String paymentUrl
) {}
