package ecommerce.features.category.service;

import ecommerce.core.domain.Category;
import ecommerce.core.exceptions.BusinessException;
import ecommerce.core.exceptions.ResourceNotFoundException;
import ecommerce.features.category.dto.CategoryRequest;
import ecommerce.features.category.dto.CategoryResponse;
import ecommerce.features.category.mappers.CategoryMapper;
import ecommerce.features.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Electronics").active(true).build();
        categoryResponse = new CategoryResponse(1L, "Electronics", true);
    }

    // ─── getAllCategories ──────────────────────────────────────────────────

    @Nested
    @DisplayName("getAllCategories")
    class GetAllCategories {

        @Test
        @DisplayName("returns mapped list of active categories")
        void givenActiveCategories_whenGetAll_thenReturnsDtoList() {
            // Given
            when(categoryRepository.findByActiveTrue()).thenReturn(List.of(category));
            when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

            // When
            List<CategoryResponse> result = categoryService.getAllCategories();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Electronics");
        }
    }

    // ─── getCategoryById ──────────────────────────────────────────────────

    @Nested
    @DisplayName("getCategoryById")
    class GetCategoryById {

        @Test
        @DisplayName("returns category when it exists and is active")
        void givenActiveCategory_whenGetById_thenReturnsDto() {
            // Given
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

            // When
            CategoryResponse result = categoryService.getCategoryById(1L);

            // Then
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when category is absent or inactive")
        void givenInactiveCategory_whenGetById_thenThrowsResourceNotFoundException() {
            // Given — category exists but is inactive
            category.setActive(false);
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            // When / Then
            assertThatThrownBy(() -> categoryService.getCategoryById(1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("1");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when category does not exist at all")
        void givenMissingCategory_whenGetById_thenThrowsResourceNotFoundException() {
            // Given
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> categoryService.getCategoryById(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ─── createCategory ───────────────────────────────────────────────────

    @Nested
    @DisplayName("createCategory")
    class CreateCategory {

        @Test
        @DisplayName("creates and returns category")
        void givenValidRequest_whenCreate_thenReturnsSavedDto() {
            // Given
            CategoryRequest request = new CategoryRequest("Electronics");
            when(categoryMapper.toEntity(request)).thenReturn(category);
            when(categoryRepository.save(category)).thenReturn(category);
            when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

            // When
            CategoryResponse result = categoryService.createCategory(request);

            // Then
            assertThat(result.getName()).isEqualTo("Electronics");
            verify(categoryRepository).save(category);
        }
    }

    // ─── softDeleteCategory ───────────────────────────────────────────────

    @Nested
    @DisplayName("softDeleteCategory")
    class SoftDeleteCategory {

        @Test
        @DisplayName("sets active=false and saves")
        void givenActiveCategory_whenSoftDelete_thenSetsActiveFalse() {
            // Given
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.save(category)).thenReturn(category);

            // When
            categoryService.softDeleteCategory(1L);

            // Then
            assertThat(category.isActive()).isFalse();
            verify(categoryRepository).save(category);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when category is absent or inactive")
        void givenMissingCategory_whenSoftDelete_thenThrowsResourceNotFoundException() {
            // Given
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> categoryService.softDeleteCategory(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(categoryRepository, never()).save(any());
        }
    }

    // ─── reactivateCategory ───────────────────────────────────────────────

    @Nested
    @DisplayName("reactivateCategory")
    class ReactivateCategory {

        @Test
        @DisplayName("throws BusinessException when category is already active")
        void givenAlreadyActiveCategory_whenReactivate_thenThrowsBusinessException() {
            // Given — category.active = true (default in setUp)
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            // When / Then
            assertThatThrownBy(() -> categoryService.reactivateCategory(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("already active");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when category is absent or inactive")
        void givenInactiveCategory_whenReactivate_thenThrowsResourceNotFoundException() {
            // Given — category is inactive, filter() will reject it → orElseThrow fires
            category.setActive(false);
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            // When / Then
            assertThatThrownBy(() -> categoryService.reactivateCategory(1L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
