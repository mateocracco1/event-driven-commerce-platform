package ecommerce.features.inventory.dto;

import ecommerce.features.inventory.domain.StockMovementReason;
import jakarta.validation.constraints.NotNull;

public class InventoryAdjustmentRequest {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;

    @NotNull(message = "La cantidad es obligatoria")
    private Integer quantity; // El admin mandará 10 para sumar, o -5 para restar

    @NotNull(message = "La razón del ajuste es obligatoria")
    private StockMovementReason reason;

    // Getters y Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public StockMovementReason getReason() { return reason; }
    public void setReason(StockMovementReason reason) { this.reason = reason; }

}
