package ecommerce.features.inventory;

import ecommerce.core.domain.Product;
import ecommerce.features.inventory.domain.StockMovementReason;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity; // Positivo para ingresos, negativo para egresos

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StockMovementReason reason;

    @Column(nullable = false)
    private String username; // Quién hizo el movimiento (Admin o "SYSTEM")

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }




}
