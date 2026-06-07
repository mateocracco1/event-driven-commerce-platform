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
    @Mapping(target = "lowStock", expression = "java(product.getStock() != null && product.getStock() <= product.getMinStock())")
    ProductResponse toResponse(Product product);

    // (Entrada - Creación)
    // CORREGIDO: YA NO IGNORAMOS EL CODE AQUÍ, permitimos que se mapee automáticamente
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductRequest request);

    // (Entrada - Actualización)
    // Aquí SÍ es correcto ignorar el code para que no se altere lo ya guardado
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true) // Movimos el ignore a donde corresponde!
    @Mapping(target = "category", ignore = true)
    void updateProductFromDto(ProductUpdateDTO request, @MappingTarget Product product);
}

