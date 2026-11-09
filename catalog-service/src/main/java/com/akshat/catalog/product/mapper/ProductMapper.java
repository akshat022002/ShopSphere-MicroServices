package com.akshat.catalog.product.mapper;

import com.akshat.catalog.product.dto.ProductCreateRequest;
import com.akshat.catalog.product.dto.ProductResponse;
import com.akshat.catalog.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "sellerId",ignore = true)
    @Mapping(target = "active",ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "subcategory", ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    @Mapping(target = "updatedAt",ignore = true)
    Product toEntity(ProductCreateRequest request);


    @Mapping(target = "categoryId",source = "category.id")
    @Mapping(target = "subcategoryId",source = "subcategory.id")
    ProductResponse toResponse(Product product);
}