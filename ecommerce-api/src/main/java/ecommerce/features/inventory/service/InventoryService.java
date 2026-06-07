package ecommerce.features.inventory.service;

import ecommerce.core.domain.Product;
import ecommerce.features.inventory.StockMovement;
import ecommerce.features.inventory.domain.StockMovementReason;
import ecommerce.features.inventory.repository.StockMovementRepository;
import ecommerce.features.products.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    public InventoryService(ProductRepository productRepository, StockMovementRepository stockMovementRepository) {
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Transactional
    public void registerMovement(Long productId, Integer quantity, StockMovementReason reason, String username) {
        // 1. Buscar el producto usando el repositorio de la otra feature
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

        // 2. Calcular y validar el nuevo stock
        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new RuntimeException("Stock insuficiente. El stock actual es " + product.getStock() + " y se intentó restar " + Math.abs(quantity));
        }

        // 3. Actualizar y guardar el producto
        product.setStock(newStock);
        productRepository.save(product);

        // 4. Registrar el movimiento en el historial para auditoría
        StockMovement movement = StockMovement.builder()
                .product(product)
                .quantity(quantity)
                .reason(reason)
                .username(username)
                .build();

        stockMovementRepository.save(movement);
    }

}
