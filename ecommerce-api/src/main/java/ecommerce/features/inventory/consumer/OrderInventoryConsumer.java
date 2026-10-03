package ecommerce.features.inventory.consumer;

import ecommerce.config.RabbitMQConfig;
import ecommerce.core.domain.Product;
import ecommerce.features.inventory.domain.StockMovementReason;
import ecommerce.features.inventory.service.InventoryService;
import ecommerce.features.order.event.OrderPaidEvent;
import ecommerce.features.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderInventoryConsumer {

    private final InventoryService inventoryService;

    @RabbitListener(queues = RabbitMQConfig.INVENTORY_ORDER_QUEUE)
    public void handleOrderPaid(OrderPaidEvent event) {
        log.info("Procesando descuento de stock para Order Number: {}", event.orderNumber());

        for (OrderPaidEvent.OrderItemPayload item : event.items()) {
            // Nota: se pasa en negativo (-item.quantity()) porque es un egreso
            inventoryService.registerMovement(
                    item.productId(),
                    -item.quantity(),
                    StockMovementReason.CUSTOMER_SALE,
                    "SYSTEM (Order #" + event.orderNumber() + ")"
            );
        }

        log.info("Stock descontado y ledger actualizado con éxito para la orden {}", event.orderNumber());
    }

}
