package com.akshat.catalog.product.controller;

import com.akshat.catalog.common.response.ApiResponse;
import com.akshat.catalog.product.dto.*;
import com.akshat.catalog.product.service.ProductService;
import com.akshat.catalog.security.userprovider.CurrentUserProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final CurrentUserProvider currentUserProvider;

    @Value("${internal.service-secret}")
    private String serviceSecret;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductCreateRequest request
                                                                        , Authentication authentication){



        ProductResponse response = productService.createProduct(request,
                currentUserProvider.getUserId(authentication),
                 currentUserProvider.getUserRole(authentication)
        );

        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>(true,
                "Product created successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiResponse);

    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER','CUSTOMER')")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long productId){

        ProductResponse response = productService.getProductById(productId);

        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>(true
        ,"Product fetched successfully"
        ,response
        ,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PatchMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@Valid @RequestBody ProductUpdateRequest request,
                                                                           @PathVariable Long productId,
                                                                           Authentication authentication){



        ProductResponse response = productService.
                updateProduct(productId
                ,currentUserProvider.getUserId(authentication)
                ,request
                ,currentUserProvider.getUserRole(authentication));

        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>(true
                ,"Product updated successfully"
                ,response
                ,LocalDateTime.now());

        return  ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long productId,Authentication authentication){


        productService.deleteProduct(productId,
                currentUserProvider.getUserId(authentication),
                currentUserProvider.getUserRole(authentication));

        ApiResponse<Void> apiResponse = new ApiResponse<>(true
                ,"Product soft deleted successfully"
                ,null
                ,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);

    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SELLER','ADMIN','CUSTOMER')")
    public ResponseEntity<ApiResponse<ProductSearchResponse>> searchProducts(@Valid ProductSearchRequest request){

        ProductSearchResponse response = productService.searchProducts(request);

        ApiResponse<ProductSearchResponse> apiResponse = new ApiResponse<>(true,
                "Products fetched successfully",
                response,LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{productId}/seller/{sellerId}")
    public ResponseEntity<Boolean> isProductOwnedBySeller(
            @PathVariable Long productId,
            @PathVariable Long sellerId) {

        boolean owned = productService.isProductOwnedBySeller(
                productId,
                sellerId
        );

        return ResponseEntity.ok(owned);
    }

    @GetMapping("dashboard/admin")
    public ResponseEntity<ApiResponse<Long>> getTotalProducts(@RequestHeader("X-Service-Secret")
                                                              String requestedSecret){

        if(!requestedSecret.equals(serviceSecret)){
            log.info("Stopped");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

       Long totalProducts = productService.totalProducts();

       ApiResponse<Long> apiResponse = new ApiResponse<>(
               true,
               "Total products fetched successfully",
               totalProducts,
               LocalDateTime.now());

       return ResponseEntity.ok(apiResponse);

    }

}
