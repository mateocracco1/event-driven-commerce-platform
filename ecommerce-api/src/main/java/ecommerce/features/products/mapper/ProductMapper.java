package ecommerce.features.products.mapper;

import ecommerce.core.domain.Product;
import ecommerce.features.products.dto.ProductRequest;
import ecommerce.features.products.dto.ProductResponse;
import ecommerce.features.products.dto.ProductUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // (Salida)
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "lowStock", expression = "java(product.getStock() <= product.getMinStock())") // Lo calcula al vuelo!
    ProductResponse toResponse(Product product);

    // (Entrada - Creación)
    // (Entrada - Actualización, sin code)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductRequest request);

    //  ACTUALIZAR
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateProductFromDto(ProductUpdateDTO request, @MappingTarget Product product);
}

