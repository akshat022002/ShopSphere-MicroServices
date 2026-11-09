package com.akshat.inventory.inventory.entity;

import com.akshat.inventory.inventory.enums.StockOperationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "inventory_stock_operations",
       uniqueConstraints = {
        @UniqueConstraint(name = "uk_stock_operation_order_product_type",
                          columnNames = {"order_id","product_id","operation_type"})
       })
public class InventoryOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false,length = 30)
    private StockOperationType operationType;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

}
