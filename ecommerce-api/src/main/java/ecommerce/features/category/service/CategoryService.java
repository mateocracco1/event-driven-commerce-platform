package ecommerce.features.category.service;

import ecommerce.core.domain.Category;
import ecommerce.core.exceptions.BusinessException;
import ecommerce.core.exceptions.ResourceNotFoundException;
import ecommerce.features.category.dto.CategoryRequest;
import ecommerce.features.category.dto.CategoryResponse;
import ecommerce.features.category.mappers.CategoryMapper;
import ecommerce.features.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByActiveTrue().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        return categoryMapper.toResponse(findCategoryActiveOrThrow(id));
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findCategoryActiveOrThrow(id);
        categoryMapper.updateCategoryFromDto(request, category);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void softDeleteCategory(Long id) {
        Category category = findCategoryActiveOrThrow(id);
        category.setActive(false);
        categoryRepository.save(category);
    }

    @Transactional
    public void reactivateCategory(Long id) {
        Category category  = findCategoryActiveOrThrow(id);
        if (category.isActive()) {
            throw new BusinessException("The category is already active");
        }
        category.setActive(true);
        categoryRepository.save(category);
    }

    private Category findCategoryActiveOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .filter(Category::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
    }
}
