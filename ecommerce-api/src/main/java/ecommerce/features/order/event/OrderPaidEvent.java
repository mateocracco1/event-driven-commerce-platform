package ecommerce.features.order.event;

import java.time.LocalDateTime;
import java.util.List;

public record OrderPaidEvent(
        Long orderId,
        String orderNumber,
        Long customerId,
        List<OrderItemPayload> items,
        LocalDateTime timestamp
) {
    public record OrderItemPayload(
            Long productId,
            Integer quantity
    ) {}
}
