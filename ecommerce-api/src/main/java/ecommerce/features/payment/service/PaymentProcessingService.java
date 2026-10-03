package ecommerce.features.payment.service;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import ecommerce.features.order.domain.Order;
import ecommerce.features.order.repository.OrderRepository;
import ecommerce.features.payment.domain.Payment;
import ecommerce.features.payment.domain.PaymentProvider;
import ecommerce.features.payment.domain.PaymentStatus;
import ecommerce.features.payment.event.PaymentApprovedEvent;
import ecommerce.features.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchanges.payment:payment.exchange}")
    private String paymentExchange;

    @Value("${rabbitmq.routing-keys.payment-approved:payment.approved}")
    private String paymentApprovedRoutingKey;

    @Transactional
    public void processWebhookPayment(Long paymentId) {
        String externalId = paymentId.toString();

        // 1. Idempotencia física
        if (paymentRepository.existsByExternalTransactionId(externalId)) {
            log.warn("El pago con externalTransactionId {} ya existe. Descartando notificación.", externalId);
            return;
        }

        // 2. Consulta al SDK de Mercado Pago
        PaymentClient client = new PaymentClient();
        com.mercadopago.resources.payment.Payment mpPayment;
        try {
            mpPayment = client.get(paymentId);
        } catch (MPException | MPApiException e) {
            log.error("Error al consultar pago {} en Mercado Pago: {}", paymentId, e.getMessage());
            throw new RuntimeException("Error consultando Mercado Pago", e);
        }

        if (mpPayment == null || !"approved".equalsIgnoreCase(mpPayment.getStatus())) {
            log.info("Pago {} ignorado. Estado en MP: {}", paymentId, mpPayment != null ? mpPayment.getStatus() : "null");
            return;
        }

        // 3. Obtener la orden asociada
        Long orderId = Long.valueOf(mpPayment.getExternalReference());
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada con ID: " + orderId));

        // 4. Instanciar tu entidad Payment
        Payment payment = Payment.builder()
                .order(order)
                .amount(mpPayment.getTransactionAmount())
                .currency(mpPayment.getCurrencyId() != null ? mpPayment.getCurrencyId() : "ARS")
                .provider(PaymentProvider.MERCADO_PAGO)
                .externalTransactionId(externalId)
                .status(PaymentStatus.APPROVED)
                .statusDetail(mpPayment.getStatusDetail())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Pago guardado exitosamente con ID local {} para la orden {}", savedPayment.getId(), order.getId());

        // 5. Publicar evento a RabbitMQ
        PaymentApprovedEvent event = new PaymentApprovedEvent(
                savedPayment.getId(),                     // 1. paymentId
                getOrder(order).getId(),                            // 2. orderId
                savedPayment.getExternalTransactionId(),  // 3. externalTransactionId
                savedPayment.getAmount(),                 // 4. amount
                LocalDateTime.now()                       // 5. timestamp
        );
        rabbitTemplate.convertAndSend(paymentExchange, paymentApprovedRoutingKey, event);
        log.info("PaymentApprovedEvent emitido a RabbitMQ para la orden {}", order.getId());
    }

    private static Order getOrder(Order order) {
        return order;
    }
}