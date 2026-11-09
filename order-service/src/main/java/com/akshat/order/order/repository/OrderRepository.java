package com.akshat.order.order.repository;


import com.akshat.order.order.entity.Order;
import com.akshat.order.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    Page<Order> findAllByUserId(Long userId, Pageable pageable);

    Optional<Order> findByIdAndUserId(Long orderId,Long userId);

    long countByStatus(OrderStatus status);

}
