package ecommerce.features.order.mapper;

import ecommerce.features.order.domain.Order;
import ecommerce.features.order.dto.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(target = "paymentUrl", source = "paymentUrl")
    @Mapping(target = "customerName", source = "order.customer.name")
    @Mapping(target = "customerEmail", source = "order.customer.email")
    @Mapping(target = "paymentStatus", source = "paymentStatus")
    OrderResponse toResponse(Order order, String paymentUrl, String paymentStatus);

}
