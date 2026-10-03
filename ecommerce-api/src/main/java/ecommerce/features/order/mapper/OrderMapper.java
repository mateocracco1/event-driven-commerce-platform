package ecommerce.features.order.mapper;

import ecommerce.features.order.domain.Order;
import ecommerce.features.order.domain.OrderItem;
import ecommerce.features.order.dto.OrderItemResponse;
import ecommerce.features.order.dto.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(target = "paymentUrl", source = "paymentUrl")
    OrderResponse toResponse(Order order, String paymentUrl);

    default OrderResponse toResponse(Order order) {
        return toResponse(order, null);
    }

}
