package ecommerce.features.products.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal price;
    private String categoryName;
}