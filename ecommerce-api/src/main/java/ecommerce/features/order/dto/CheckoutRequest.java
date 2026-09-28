package ecommerce.features.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CheckoutRequest(
        @NotEmpty(message = "La orden debe contener al menos un producto")
        @Valid
        List<OrderItemRequest> items
){}
