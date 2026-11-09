package com.akshat.order.order.mapper;

import com.akshat.order.order.dto.OrderItemResponse;
import com.akshat.order.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "productName", ignore = true)
    OrderItemResponse toResponse(OrderItem orderItem);
}