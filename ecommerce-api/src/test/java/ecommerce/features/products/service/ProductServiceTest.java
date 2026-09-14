package ecommerce.features.products.service;

import ecommerce.core.domain.Category;
import ecommerce.core.domain.Product;
import ecommerce.core.exceptions.BusinessException;
import ecommerce.core.exceptions.ResourceNotFoundException;
import ecommerce.features.category.repository.CategoryRepository;
import ecommerce.features.products.dto.ProductRequest;
import ecommerce.features.products.dto.ProductResponse;
import ecommerce.features.products.mapper.ProductMapper;
import ecommerce.features.products.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Category category;
    private Product product;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Electronics").active(true).build();

        product = Product.builder()
                .id(1L)
                .code("PROD-01")
                .name("Laptop")
                .price(BigDecimal.valueOf(999.99))
                .stock(10)
                .active(true)
                .category(category)
                .minStock(2)
                .build();

        productResponse = ProductResponse.builder()
                .id(1L)
                .code("PROD-01")
                .name("Laptop")
                .price(BigDecimal.valueOf(999.99))
                .stock(10)
                .categoryName("Electronics")
                .build();
    }

    // ─── getAllProducts ────────────────────────────────────────────────────

    @Nested
    @DisplayName("getAllProducts")
    class GetAllProducts {

        @Test
        @DisplayName("returns mapped list of active products")
        void givenActiveProducts_whenGetAll_thenReturnsDtoList() {
            // Given
            when(productRepository.findByActiveTrue()).thenReturn(List.of(product));
            when(productMapper.toResponse(product)).thenReturn(productResponse);

            // When
            List<ProductResponse> result = productService.getAllProducts();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Laptop");
        }
    }

    // ─── getProductById ────────────────────────────────────────────────────

    @Nested
    @DisplayName("getProductById")
    class GetProductById {

        @Test
        @DisplayName("returns product when it exists and is active")
        void givenActiveProduct_whenGetById_thenReturnsDto() {
            // Given
            when(productRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(product));
            when(productMapper.toResponse(product)).thenReturn(productResponse);

            // When
            ProductResponse result = productService.getProductById(1L);

            // Then
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when product is absent or inactive")
        void givenMissingProduct_whenGetById_thenThrowsResourceNotFoundException() {
            // Given
            when(productRepository.findByIdAndActiveTrue(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> productService.getProductById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // ─── createProduct ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("createProduct")
    class CreateProduct {

        @Test
        @DisplayName("creates and returns product when category exists")
        void givenValidRequest_whenCreate_thenReturnsSavedDto() {
            // Given
            ProductRequest request = ProductRequest.builder()
                    .code("PROD-01").name("Laptop").price(BigDecimal.valueOf(999.99))
                    .stock(10).categoryId(1L).minStock(2).build();

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(productMapper.toEntity(request)).thenReturn(product);
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toResponse(product)).thenReturn(productResponse);

            // When
            ProductResponse result = productService.createProduct(request);

            // Then
            assertThat(result.getCode()).isEqualTo("PROD-01");
            verify(productRepository).save(product);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when category does not exist")
        void givenMissingCategory_whenCreate_thenThrowsResourceNotFoundException() {
            // Given
            ProductRequest request = ProductRequest.builder()
                    .code("PROD-01").name("Laptop").price(BigDecimal.valueOf(999.99))
                    .stock(10).categoryId(99L).minStock(2).build();

            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> productService.createProduct(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
            verify(productRepository, never()).save(any());
        }
    }

    // ─── softDeleteProduct ─────────────────────────────────────────────────

    @Nested
    @DisplayName("softDeleteProduct")
    class SoftDeleteProduct {

        @Test
        @DisplayName("sets active=false and saves")
        void givenActiveProduct_whenSoftDelete_thenSetsActiveFalse() {
            // Given
            when(productRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(product)).thenReturn(product);

            // When
            productService.softDeleteProduct(1L);

            // Then
            assertThat(product.isActive()).isFalse();
            verify(productRepository).save(product);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when product is absent or already inactive")
        void givenMissingProduct_whenSoftDelete_thenThrowsResourceNotFoundException() {
            // Given
            when(productRepository.findByIdAndActiveTrue(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> productService.softDeleteProduct(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(productRepository, never()).save(any());
        }
    }

    // ─── reactivateProduct ─────────────────────────────────────────────────

    @Nested
    @DisplayName("reactivateProduct")
    class ReactivateProduct {

        @Test
        @DisplayName("sets active=true when product is currently inactive")
        void givenInactiveProduct_whenReactivate_thenSetsActiveTrue() {
            // Given
            product.setActive(false);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(product)).thenReturn(product);

            // When
            productService.reactivateProduct(1L);

            // Then
            assertThat(product.isActive()).isTrue();
        }

        @Test
        @DisplayName("throws BusinessException when product is already active")
        void givenAlreadyActiveProduct_whenReactivate_thenThrowsBusinessException() {
            // Given — product.active = true (default in setUp)
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            // When / Then
            assertThatThrownBy(() -> productService.reactivateProduct(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("already active");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when product does not exist")
        void givenMissingProduct_whenReactivate_thenThrowsResourceNotFoundException() {
            // Given
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> productService.reactivateProduct(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
