package ecommerce.features.category.repository;

import ecommerce.core.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    List<Category> findByActiveTrue();
    Optional<Category> findByIdAndActiveTrue(Long id);
    boolean existsByName(String name);

}
