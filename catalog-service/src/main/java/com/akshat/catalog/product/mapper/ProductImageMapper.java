package com.akshat.catalog.product.mapper;

import com.akshat.catalog.product.dto.ProductImageResponse;
import com.akshat.catalog.product.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    @Mapping(target = "isPrimary",source = "primary")
    public ProductImageResponse toResponse(ProductImage productImage);
}
