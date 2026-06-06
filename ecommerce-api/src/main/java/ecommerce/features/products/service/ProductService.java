package ecommerce.features.products.service;

import ecommerce.core.domain.Category;
import ecommerce.core.domain.Product;
import ecommerce.features.category.repository.CategoryRepository;
import ecommerce.features.products.ProductSpecifications;
import ecommerce.features.products.dto.ProductUpdateDTO;
import ecommerce.features.products.repository.ProductRepository;
import ecommerce.features.products.dto.ProductRequest;
import ecommerce.features.products.dto.ProductResponse;
import ecommerce.features.products.mapper.ProductMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
    }


    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findByActiveTrue().stream().map(productMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return productMapper.toResponse(findProductActiveOrThrow(id));
    }


    public ProductResponse updateProduct(Long id, ProductUpdateDTO dto) {
        Product product = findProductActiveOrThrow(id);
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        productMapper.updateProductFromDto(dto, product);
        product.setCategory(category);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        return productMapper.toResponse(productRepository.save(product));
    }


    @Transactional
    public void softDeleteProduct(Long id) {
        Product product = findProductActiveOrThrow(id);
        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional
    public void reactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        if (product.isActive()) {
            throw new RuntimeException("El producto ya está activo");
        }
        product.setActive(true);
        productRepository.save(product);
    }

    private Product findProductActiveOrThrow(Long productId) {
        return productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado o inactivo con ID: " + productId));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String search, Long categoryId, String direction) {
        // 1. Configurar el orden dinámico por el campo "price"
        Sort sort = Sort.by(Sort.Direction.ASC, "price"); // Por defecto menor precio
        if ("desc".equalsIgnoreCase(direction)) {
            sort = Sort.by(Sort.Direction.DESC, "price"); // Mayor precio
        }

        // 2. Combinar las especificaciones dinámicas
        Specification<Product> spec = ProductSpecifications.filterProducts(search, categoryId);

        // 3. Ejecutar la consulta en la base de datos con filtros y orden, pero sin paginar
        return productRepository.findAll(spec, sort).stream()
                .map(productMapper::toResponse)
                .toList();
    }
}
