package ecommerce.features.products.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01")
    private BigDecimal price;

    @NotNull(message = "El stock es obligatorio")
    @Min(0)
    private Integer stock;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;
}