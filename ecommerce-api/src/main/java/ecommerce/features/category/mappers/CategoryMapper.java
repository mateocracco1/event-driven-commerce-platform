package ecommerce.features.category.mappers;

import ecommerce.core.domain.Category;
import ecommerce.features.category.dto.CategoryRequest;
import ecommerce.features.category.dto.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Convierte la entidad a DTO de salida (id, name, active)
    CategoryResponse toResponse(Category category);

    // Convierte el DTO de entrada a Entidad para persistir por primera vez
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true) // Se maneja por defecto en la DB o service
    Category toEntity(CategoryRequest request);

    // Actualiza la categoría existente usando el mismo Request de entrada
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateCategoryFromDto(CategoryRequest request, @MappingTarget Category category);
}



