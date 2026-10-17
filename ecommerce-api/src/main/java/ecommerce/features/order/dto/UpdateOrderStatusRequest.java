package ecommerce.features.order.dto;

import ecommerce.features.order.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull(message = "El estado es requerido")
        OrderStatus status
) {}
