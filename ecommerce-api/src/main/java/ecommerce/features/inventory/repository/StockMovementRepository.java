package ecommerce.features.inventory.repository;

import ecommerce.features.inventory.domain.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

// para futuro ver historial de movimientos de un producto
//    List<StockMovement> findByProductIdOrderByCreatedAtDesc(Long productId);

}

