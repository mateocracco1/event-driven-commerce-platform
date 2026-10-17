package ecommerce.features.order.service;

import ecommerce.core.domain.Product;
import ecommerce.features.order.domain.Order;
import ecommerce.features.order.domain.OrderItem;
import ecommerce.features.order.domain.OrderStatus;
import ecommerce.features.order.dto.CheckoutRequest;
import ecommerce.features.order.dto.OrderItemRequest;
import ecommerce.features.order.dto.OrderResponse;
import ecommerce.features.order.mapper.OrderMapper;
import ecommerce.features.order.repository.OrderRepository;
import ecommerce.features.payment.infrastructure.mercadopago.MercadoPagoPreferenceService;
import ecommerce.features.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    private final MercadoPagoPreferenceService mercadoPagoPreferenceService;

    @Transactional
    public OrderResponse checkout(Long customerId, CheckoutRequest request) {
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .status(OrderStatus.PENDING_PAYMENT)
                .customerId(customerId)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            // 1. Validar existencia del producto
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + itemRequest.productId()));

            // 2. Validar stock disponible
            if (product.getStock() < itemRequest.quantity()) {
                throw new IllegalStateException("Stock insuficiente para el producto: " + product.getName()
                        + ". Disponible: " + product.getStock() + ", Solicitado: " + itemRequest.quantity());
            }

            // 3. Snapshot de precios y cálculo de subtotal
            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.quantity()));

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(itemRequest.quantity())
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .build();

            // 4. Agregar a la orden (el helper se encarga de setOrder)
            order.addItem(orderItem);
            total = total.add(subtotal);
        }

        order.setTotalAmount(total);

        // 5. Persistir (CascadeType.ALL guarda los items automáticamente)
        Order savedOrder = orderRepository.save(order);

        // 6. Generar la Preference en Mercado Pago con el order.id generado
        String paymentUrl = mercadoPagoPreferenceService.createPaymentPreference(savedOrder);

        return orderMapper.toResponse(savedOrder, paymentUrl);    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada con ID: " + id));
        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, ecommerce.features.order.dto.UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada con ID: " + id));
        order.setStatus(request.status());
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "ORD-" + timestamp + "-" + randomSuffix;
    }
}