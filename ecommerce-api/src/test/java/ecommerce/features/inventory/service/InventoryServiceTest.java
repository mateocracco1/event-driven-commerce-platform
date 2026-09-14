package ecommerce.features.inventory.service;

import ecommerce.core.domain.Product;
import ecommerce.core.exceptions.BusinessException;
import ecommerce.core.exceptions.ResourceNotFoundException;
import ecommerce.features.inventory.domain.StockMovement;
import ecommerce.features.inventory.domain.StockMovementReason;
import ecommerce.features.inventory.repository.StockMovementRepository;
import ecommerce.features.products.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService")
class InventoryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .code("PROD-01")
                .name("Laptop")
                .price(BigDecimal.valueOf(999.99))
                .stock(10)
                .active(true)
                .minStock(2)
                .build();
    }

    // ─── registerMovement ─────────────────────────────────────────────────

    @Nested
    @DisplayName("registerMovement")
    class RegisterMovement {

        @Test
        @DisplayName("adds stock and saves movement record when quantity is positive")
        void givenPositiveQuantity_whenRegisterMovement_thenStockIsIncreased() {
            // Given
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenReturn(product);

            // When
            inventoryService.registerMovement(1L, 5, StockMovementReason.SUPPLIER_REFRESH, "admin");

            // Then
            assertThat(product.getStock()).isEqualTo(15);
            verify(productRepository).save(product);
            verify(stockMovementRepository).save(any(StockMovement.class));
        }

        @Test
        @DisplayName("subtracts stock and saves movement record when quantity is negative")
        void givenNegativeQuantity_whenRegisterMovement_thenStockIsDecreased() {
            // Given
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenReturn(product);

            // When
            inventoryService.registerMovement(1L, -3, StockMovementReason.CUSTOMER_SALE, "admin");

            // Then
            assertThat(product.getStock()).isEqualTo(7);
        }

        @Test
        @DisplayName("saves movement with correct fields")
        void givenValidMovement_whenRegisterMovement_thenMovementHasCorrectData() {
            // Given
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenReturn(product);
            ArgumentCaptor<StockMovement> captor = ArgumentCaptor.forClass(StockMovement.class);

            // When
            inventoryService.registerMovement(1L, 5, StockMovementReason.MANUAL_ADJUSTMENT, "operator");

            // Then
            verify(stockMovementRepository).save(captor.capture());
            StockMovement saved = captor.getValue();
            assertThat(saved.getQuantity()).isEqualTo(5);
            assertThat(saved.getReason()).isEqualTo(StockMovementReason.MANUAL_ADJUSTMENT);
            assertThat(saved.getUsername()).isEqualTo("operator");
        }

        @Test
        @DisplayName("throws BusinessException when resulting stock would be negative")
        void givenQuantityExceedingStock_whenRegisterMovement_thenThrowsBusinessException() {
            // Given — stock is 10, trying to subtract 15
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            // When / Then
            assertThatThrownBy(() ->
                    inventoryService.registerMovement(1L, -15, StockMovementReason.CUSTOMER_SALE, "admin"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Insufficient stock");

            verify(productRepository, never()).save(any());
            verify(stockMovementRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when product does not exist")
        void givenMissingProduct_whenRegisterMovement_thenThrowsResourceNotFoundException() {
            // Given
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() ->
                    inventoryService.registerMovement(99L, 5, StockMovementReason.SUPPLIER_REFRESH, "admin"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(stockMovementRepository, never()).save(any());
        }
    }
}
