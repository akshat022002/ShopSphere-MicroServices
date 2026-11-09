package com.akshat.catalog.product.service;

import com.akshat.catalog.common.enums.Role;
import com.akshat.catalog.product.dto.ProductImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {

    ProductImageResponse uploadImage(Long productId, Long userId, MultipartFile file,boolean isPrimary);

    List<ProductImageResponse> getProductImages(Long productId);

    void deleteImage(Long productId,Long imageId,Long userId, Role role);

    void setPrimaryImage(Long productId,Long imageId,Long userId, Role role);
}
