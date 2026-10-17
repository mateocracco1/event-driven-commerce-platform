package ecommerce.features.payment.repository;

import ecommerce.features.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Optional<Payment> findByExternalTransactionId(String externalTransactionId);

    boolean existsByExternalTransactionId(String string);
}
