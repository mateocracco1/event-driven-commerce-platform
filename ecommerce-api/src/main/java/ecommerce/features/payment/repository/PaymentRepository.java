package ecommerce.features.payment.repository;

import ecommerce.features.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Optional<Payment> findByExternalTransactionId(String externalTransactionId);

    boolean existsByExternalTransactionId(String string);

    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId ORDER BY p.createdAt DESC")
    List<Payment> findLatestByOrderId(@Param("orderId") Long orderId);
}
