package ecommerce.features.products;

import ecommerce.core.domain.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class ProductSpecifications {


    public static Specification<Product> filterProducts(String search, Long categoryId) {
        return (root, query, cb) -> {
            // Condición base: SIEMPRE deben estar activos
            var predicate = cb.equal(root.get("active"), true);

            // Si viene el parámetro de búsqueda (busca por nombre O código)
            if (StringUtils.hasText(search)) {
                var nameLike = cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
                var codeLike = cb.like(cb.lower(root.get("code")), "%" + search.toLowerCase() + "%");
                var searchPredicate = cb.or(nameLike, codeLike);
                predicate = cb.and(predicate, searchPredicate);
            }

            // Si viene filtro por categoría
            if (categoryId != null) {
                var categoryPredicate = cb.equal(root.get("category").get("id"), categoryId);
                predicate = cb.and(predicate, categoryPredicate);
            }

            return predicate;
        };
    }


}
