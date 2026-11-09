package com.akshat.inventory.inventory.repository;

import com.akshat.inventory.inventory.entity.InventoryOperation;
import com.akshat.inventory.inventory.enums.StockOperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryStockOperationRepository extends JpaRepository<InventoryOperation,Long> {
    /**
    This particular repo method insert row into db in  accordance
     with the db constraint of InventoryOperation Entity

     and returns an int 1 or 0 in accordance with method success/failure
     for ex - If a row already exists it returns 0 otherwise it returns 1
     */
    @Modifying
    @Query(value = """
            INSERT INTO inventory_stock_operations
            (order_id, product_id, operation_type, quantity, created_at)
            VALUES
            (:orderId, :productId, :operationType, :quantity, CURRENT_TIMESTAMP)
            ON CONFLICT (order_id, product_id, operation_type)
            DO NOTHING
            """,nativeQuery = true)
    int insertIfNotExists(
            @Param("orderId") Long orderId,
            @Param("productId") Long productId,
            @Param("operationType") String operationType,
            @Param("quantity") Integer quantity
            );

    Optional<InventoryOperation> findByOrderIdAndProductIdAndOperationType(
            Long orderId,
            Long productId,
            StockOperationType operationType);

}
