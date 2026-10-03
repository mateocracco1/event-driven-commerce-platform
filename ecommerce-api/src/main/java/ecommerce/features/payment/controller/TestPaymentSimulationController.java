package ecommerce.features.payment.controller;
import ecommerce.config.RabbitMQConfig;
import ecommerce.features.payment.event.PaymentApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Profile({"dev", "test"})
@Slf4j
@RestController
@RequestMapping("/api/payments/test")
@RequiredArgsConstructor
public class TestPaymentSimulationController {

    private final RabbitTemplate rabbitTemplate;

    @PostMapping("/simulate-approval/{orderId}")
    public ResponseEntity<String> simulateApproval(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "1000.00") BigDecimal amount
    ) {
        String fakeExternalTxId = "SIM-" + UUID.randomUUID().toString().substring(0, 8);

        PaymentApprovedEvent event = new PaymentApprovedEvent(
                1L, // ID simulado de pago
                orderId,
                fakeExternalTxId,
                amount,
                LocalDateTime.now()
        );

        log.info("Simulando aprobación de pago para la orden ID: {} con TX: {}", orderId, fakeExternalTxId);

        // Disparo del evento al exchange de pagos
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_APPROVED_ROUTING_KEY,
                event
        );

        return ResponseEntity.ok("Evento simulado emitido con éxito para la orden " + orderId + " con TX: " + fakeExternalTxId);
    }
}