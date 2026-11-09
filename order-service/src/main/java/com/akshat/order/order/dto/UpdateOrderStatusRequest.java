package com.akshat.order.order.dto;

import com.akshat.order.order.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull
        OrderStatus status
) {
}
