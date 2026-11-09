package com.akshat.catalog.category.dto;

import jakarta.validation.constraints.Size;

public record CategoryUpdateRequest(

        @Size(max = 100,message = "Category must not exceed 100 characters")
        String name,

        @Size(max =500,message = "Category must not exceed 500 characters")
        String description
) {
}
