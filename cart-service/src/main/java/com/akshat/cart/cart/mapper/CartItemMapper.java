package com.akshat.cart.cart.mapper;

import com.akshat.cart.cart.dto.CartItemResponse;
import com.akshat.cart.cart.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    CartItemResponse toResponse(CartItem cartItem);
}