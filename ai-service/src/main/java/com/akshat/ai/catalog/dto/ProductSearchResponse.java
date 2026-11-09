package com.akshat.ai.catalog.dto;

import com.akshat.ai.ai.dto.ProductAiResponse;

import java.util.List;

public record ProductSearchResponse(
        List<ProductAiResponse> content,
        int page,
        int size
) {
}
