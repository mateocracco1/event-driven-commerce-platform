package ecommerce.features.products.service;

import ecommerce.core.domain.Category;
import ecommerce.core.domain.Product;
import ecommerce.core.repository.CategoryRepository;
import ecommerce.core.repository.ProductRepository;
import ecommerce.features.products.dto.ProductRequest;
import ecommerce.features.products.dto.ProductResponse;
import ecommerce.features.products.mapper.ProductMapper;
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
        return productRepository.findAll().stream().map(productMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return productMapper.toResponse(findProductOrThrow(id));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest dto) {
        Product product = findProductOrThrow(id);
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
    public void deleteProductById(Long id) {
        Product product = findProductOrThrow(id);
        productRepository.delete(product);
    }

    private Product findProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));
    }




}
