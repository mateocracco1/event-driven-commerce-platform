package ecommerce.features.payment.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentApprovedEvent(
        Long paymentId,
        Long orderId,
        String externalTransactionId,
        BigDecimal amount,
        LocalDateTime timestamp
) {
}
