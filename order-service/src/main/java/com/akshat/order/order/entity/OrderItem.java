package com.akshat.order.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_items",
       indexes = {
        @Index(name ="idx_order_item_order",columnList ="order_id"),
        @Index(name ="idx_order_item_product",columnList ="product_id")
       })
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",nullable = false)
    private Order order;

    @Column(name = "product_id",nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal subtotal;

}
