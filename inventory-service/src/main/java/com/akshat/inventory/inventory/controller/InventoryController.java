package com.akshat.inventory.inventory.controller;

import com.akshat.inventory.common.enums.Role;
import com.akshat.inventory.common.response.ApiResponse;
import com.akshat.inventory.security.userprovider.CurrentUserProvider;
import com.akshat.inventory.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final CurrentUserProvider currentUserProvider;

    @Value("${internal.service-secret}")
    private String serviceSecret;


    @PatchMapping("/{productId}/increase")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<Void>> increaseStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity,
            @RequestHeader("Authorization") String token,
            Authentication authentication) {

        Long userId = currentUserProvider.getUserId(authentication);
        Role role = currentUserProvider.getUserRole(authentication);

        inventoryService.increaseStock(
                productId,
                userId,
                role,
                quantity,
                token
        );

        ApiResponse<Void> apiResponse = new ApiResponse<>(
                true,
                "Stock increased successfully",
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{productId}/decrease")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<Void>> decreaseStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity,
            @RequestHeader("Authorization") String token,
            Authentication authentication) {

        Long userId = currentUserProvider.getUserId(authentication);
        Role role = currentUserProvider.getUserRole(authentication);

        inventoryService.decreaseStock(
                productId,
                userId,
                role,
                quantity,
                token
        );

        ApiResponse<Void> apiResponse = new ApiResponse<>(
                true,
                "Stock decreased successfully",
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER','CUSTOMER')")
    public ResponseEntity<ApiResponse<Integer>> getStock(
            @PathVariable Long productId) {

        Integer stock = inventoryService.getStock(productId);

        ApiResponse<Integer> apiResponse = new ApiResponse<>(
                true,
                "Stock fetched successfully",
                stock,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(apiResponse);
    }


    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> initializeInventory(
            @PathVariable Long productId,
            @RequestHeader("Authorization") String token) {

        inventoryService.initializeInventory(productId, token);

        ApiResponse<Void> apiResponse = new ApiResponse<>(true,
                "Inventory initialized successfully",
                null,
                LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @PatchMapping("/{productId}/decrease-for-order")
    public ResponseEntity<ApiResponse<Void>> decreaseStockForOrder(@PathVariable Long productId,
                                                                   @RequestParam Integer quantity,
                                                                   @RequestParam Long orderId,
                                                                   @RequestHeader("X-Service-Secret") String requestedSecret){

        if(!serviceSecret.equals(requestedSecret)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        inventoryService.decreaseStockForOrder(productId,quantity,orderId);

        ApiResponse<Void> response = new ApiResponse<>(true,
                "Stock decreased successfully after order is placed",
                null,
                LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{productId}/increase-for-order")
    public ResponseEntity<ApiResponse<Void>> increaseStockForOrder(@PathVariable Long productId,
                                                                   @RequestParam Integer quantity,
                                                                   @RequestParam Long orderId,
                                                                   @RequestHeader("X-Service-Secret") String requestedSecret){

        if(!serviceSecret.equals(requestedSecret)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        inventoryService.increaseStockForOrder(productId,quantity,orderId);

        ApiResponse<Void> response = new ApiResponse<>(true,
                "Stock increased successfully after order is cancelled",
                null,
                LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}