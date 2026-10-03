package ecommerce.features.order.consumer;

import ecommerce.config.RabbitMQConfig;
import ecommerce.features.order.domain.Order;
import ecommerce.features.order.domain.OrderStatus;
import ecommerce.features.order.event.OrderPaidEvent;
import ecommerce.features.order.repository.OrderRepository;
import ecommerce.features.payment.event.PaymentApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.ORDER_PAYMENT_QUEUE)
    @Transactional
    public void handlePaymentApproved(PaymentApprovedEvent event) {
        log.info("Recibido PaymentApprovedEvent para Order ID: {}", event.orderId());

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + event.orderId()));

        if (order.getStatus() == OrderStatus.PAID) {
            log.warn("La orden {} ya se encuentra en estado PAID. Mensaje duplicado omitido.", order.getId());
            return; // Idempotencia lógica en el consumidor
        }

        // 1. Actualizar estado de negocio
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        log.info("Orden {} actualizada a PAID con éxito.", order.getId());

        // 2. Preparar el payload para inventario
        List<OrderPaidEvent.OrderItemPayload> itemPayloads = order.getItems().stream()
                .map(item -> new OrderPaidEvent.OrderItemPayload(item.getProductId(), item.getQuantity()))
                .toList();

        OrderPaidEvent orderPaidEvent = new OrderPaidEvent(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerId(),
                itemPayloads,
                LocalDateTime.now()
        );

        // 3. Notificar a inventario
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_PAID_ROUTING_KEY,
                orderPaidEvent
        );
        log.info("Publicado OrderPaidEvent para la orden {}", order.getOrderNumber());
    }





}
