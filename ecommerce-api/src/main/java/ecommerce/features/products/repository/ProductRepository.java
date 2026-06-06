package ecommerce.features.products.repository;

import ecommerce.core.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository  extends JpaRepository<Product,Long> , JpaSpecificationExecutor<Product> {

    List<Product> findByActiveTrue();
    Optional<Product> findByIdAndActiveTrue(Long id);
    boolean existsByCode(String code);
}

