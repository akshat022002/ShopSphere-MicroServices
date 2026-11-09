package com.akshat.order.order.mapper;

import com.akshat.order.order.dto.OrderResponse;
import com.akshat.order.order.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = OrderItemMapper.class)
public interface OrderMapper {

public OrderResponse toResponse(Order order);

}
