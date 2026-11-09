package com.akshat.inventory.inventory.service;


import com.akshat.inventory.common.enums.Role;

public interface InventoryService {

    void increaseStock(Long productId, Long userId, Role role, Integer quantity,String token);

    void decreaseStock(Long productId,Long userId,Role role, Integer quantity,String token);

    Integer getStock(Long productId);

    void increaseStockForOrder(Long productId,Integer quantity, Long orderId);

    void decreaseStockForOrder(Long productId,Integer quantity, Long orderId);

    void initializeInventory(Long productId,String token);
}
