package com.akshat.catalog.product.service;

import com.akshat.catalog.common.enums.Role;
import com.akshat.catalog.product.dto.*;

public interface ProductService {


    ProductResponse createProduct(ProductCreateRequest request , Long sellerId,Role role);

    ProductResponse getProductById(Long productId);

    ProductResponse updateProduct(Long productId, Long userId, ProductUpdateRequest request, Role role) ;

    void deleteProduct(Long productId,Long userId,Role role) ;

    ProductSearchResponse searchProducts(ProductSearchRequest request);

    boolean isProductOwnedBySeller(Long productId, Long sellerId);

    long totalProducts();

}