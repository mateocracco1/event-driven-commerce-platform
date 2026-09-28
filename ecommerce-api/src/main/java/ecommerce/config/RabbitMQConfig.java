package ecommerce.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {



    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String ORDER_EXCHANGE = "order.exchange";

    // Nombres de Colas
    public static final String ORDER_PAYMENT_QUEUE = "order.payment.approved.queue";
    public static final String INVENTORY_ORDER_QUEUE = "inventory.order.paid.queue";

    // Routing Keys
    public static final String PAYMENT_APPROVED_ROUTING_KEY = "payment.approved";
    public static final String ORDER_PAID_ROUTING_KEY = "order.paid";

    // 1. Convertidor JSON para los payloads
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // 2. Infraestructura de Payment -> Order
    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public Queue orderPaymentQueue() {
        return QueueBuilder.durable(ORDER_PAYMENT_QUEUE).build();
    }

    @Bean
    public Binding bindingPaymentApproved(Queue orderPaymentQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(orderPaymentQueue)
                .to(paymentExchange)
                .with(PAYMENT_APPROVED_ROUTING_KEY);
    }

    // 3. Infraestructura de Order -> Inventory
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue inventoryOrderQueue() {
        return QueueBuilder.durable(INVENTORY_ORDER_QUEUE).build();
    }

    @Bean
    public Binding bindingOrderPaid(Queue inventoryOrderQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(inventoryOrderQueue)
                .to(orderExchange)
                .with(ORDER_PAID_ROUTING_KEY);
    }





}
