package com.akshat.order.order.repository;

import com.akshat.order.order.entity.OrderItem;
import com.akshat.order.order.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

    @Query("""
    SELECT COUNT(oi) > 0
    FROM OrderItem oi
    WHERE oi.order.userId = :userId
    AND oi.productId = :productId
    AND oi.order.status = :status
    """)
    boolean existsByUserAndProductAndOrderStatus(
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            @Param("status") OrderStatus status
    );

}
