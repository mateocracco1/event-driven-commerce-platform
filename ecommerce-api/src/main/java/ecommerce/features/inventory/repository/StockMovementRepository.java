package ecommerce.features.inventory.repository;

import ecommerce.features.inventory.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

// para futuro ver historial de movimientos de un producto
//    List<StockMovement> findByProductIdOrderByCreatedAtDesc(Long productId);

}

