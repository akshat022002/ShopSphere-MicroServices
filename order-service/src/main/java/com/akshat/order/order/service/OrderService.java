package com.akshat.order.order.service;


import com.akshat.order.order.dto.AdminOrderStatistics;
import com.akshat.order.order.dto.OrderResponse;
import com.akshat.order.order.dto.OrderSummaryResponse;
import com.akshat.order.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse placeOrder(Long userId,String couponCode,String token,String email);

    OrderResponse getOrderById(Long userId, Long orderId);

    Page<OrderSummaryResponse> getMyOrders(Long userId, Pageable pageable);

    OrderResponse cancelOrder(Long userId,Long orderId,String email);

    void updateOrderStatus(Long orderId, OrderStatus newStatus);

    void confirmOrder(Long orderId, Long userId); //Added for payment-service

    boolean hasDeliveredProduct(Long userId,Long productId);//Added for review-service

    AdminOrderStatistics getOrderCount();

}
