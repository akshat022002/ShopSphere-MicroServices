package com.akshat.inventory.inventory.service;

import com.akshat.inventory.catalog.CatalogClient;
import com.akshat.inventory.common.enums.Role;
import com.akshat.inventory.common.exception.DuplicateResourceException;
import com.akshat.inventory.common.exception.InsufficientStockException;
import com.akshat.inventory.common.exception.ResourceNotFoundException;
import com.akshat.inventory.inventory.entity.Inventory;
import com.akshat.inventory.inventory.entity.InventoryOperation;
import com.akshat.inventory.inventory.enums.StockOperationType;
import com.akshat.inventory.inventory.repository.InventoryRepository;
import com.akshat.inventory.inventory.repository.InventoryStockOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final CatalogClient catalogClient;
    private final InventoryStockOperationRepository stockOperationRepository;


    @Override
    public void increaseStock(
            Long productId,
            Long userId,
            Role role,
            Integer quantity,
            String token) {

        validateQuantity(quantity);

        Inventory inventory =
                getAuthorizedInventory(productId, userId, role,token);

        inventory.setStockQuantity(
                inventory.getStockQuantity() + quantity
        );
    }

    @Override
    public void decreaseStock(
            Long productId,
            Long userId,
            Role role,
            Integer quantity,
            String token) {

        validateQuantity(quantity);

        Inventory inventory =
                getAuthorizedInventory(productId, userId, role,token);

        if (inventory.getStockQuantity() < quantity) {
            throw new IllegalStateException(
                    "Insufficient stock for product : " + productId
            );
        }

        inventory.setStockQuantity(
                inventory.getStockQuantity() - quantity
        );
    }

    @Transactional(readOnly = true)
    @Override
    public Integer getStock(Long productId) {

        Inventory inventory = getInventory(productId);

        return inventory.getStockQuantity();
    }

    @Override
    public void increaseStockForOrder(
            Long productId,
            Integer quantity, Long orderId) {

        validateQuantity(quantity);

        int inserted = stockOperationRepository.insertIfNotExists(
                orderId,productId, StockOperationType.RESTORE_FOR_ORDER.name(),quantity);

        if(inserted==0){
            validateDuplicateOperation(orderId,productId,StockOperationType.RESTORE_FOR_ORDER,quantity);
            return;
        }

        Inventory inventory = getInventory(productId);

        inventory.setStockQuantity(
                inventory.getStockQuantity() + quantity
        );
    }

    @Override
    public void decreaseStockForOrder(
            Long productId,
            Integer quantity,Long orderId) {

        validateQuantity(quantity);

        int inserted = stockOperationRepository.insertIfNotExists(
                orderId,productId, StockOperationType.DECREASE_FOR_ORDER.name(),quantity);

        if(inserted==0){
            validateDuplicateOperation(orderId,productId,StockOperationType.DECREASE_FOR_ORDER,quantity);
            return;
        }

        Inventory inventory = getInventory(productId);

        if (inventory.getStockQuantity() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for product : " + productId
            );
        }

        inventory.setStockQuantity(
                inventory.getStockQuantity() - quantity
        );
    }

    @Override
    public void initializeInventory(Long productId,String token) {

        if(!catalogClient.productExists(productId,token)){
            throw  new ResourceNotFoundException("Product not found with id : "+productId);
        }

        if(inventoryRepository.existsByProductId(productId)){
            throw new DuplicateResourceException("Inventory already exists for product : "+productId);
        }

        Inventory inventory = Inventory.builder()
                .productId(productId)
                .build();

        inventoryRepository.save(inventory);

    }

    private void validateDuplicateOperation(Long orderId,Long productId,StockOperationType operationType,Integer requestedQuantity){

        InventoryOperation existing = stockOperationRepository.findByOrderIdAndProductIdAndOperationType(
                orderId,
                productId,
                operationType
        ).orElseThrow(()->
                new IllegalStateException("Stock operation record not found"));

        if(!existing.getQuantity().equals(requestedQuantity)){
            throw new IllegalArgumentException("This stock operation was already processed "
                    + "with a different quantity");

        }
    }

    private Inventory getAuthorizedInventory(
            Long productId,
            Long userId,
            Role role,
            String token) {

        Inventory inventory = getInventory(productId);

        if (Role.ROLE_ADMIN.equals(role)) {
            return inventory;
        }

        if (Role.ROLE_SELLER.equals(role)) {

            if(catalogClient.isProductOwnedBySeller(productId,userId,token)){
                return inventory;
            }
        }

        throw new AccessDeniedException(
                "You are not authorized to modify stock"
        );
    }

    private Inventory getInventory(Long productId) {

        return inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory not found for product : "
                                        + productId
                        ));
    }

    private void validateQuantity(Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }
    }
}