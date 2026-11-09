package com.akshat.catalog.category.mapper;

import com.akshat.catalog.category.dto.SubcategoryCreateRequest;
import com.akshat.catalog.category.dto.SubcategoryResponse;
import com.akshat.catalog.category.entity.Subcategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubcategoryMapper {

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "category",ignore = true )
    @Mapping(target = "active",ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    @Mapping(target = "updatedAt",ignore = true)
    public Subcategory toEntity(SubcategoryCreateRequest request);

    @Mapping(target = "categoryId",source = "category.id")
    public SubcategoryResponse toResponse(Subcategory subcategory);
}