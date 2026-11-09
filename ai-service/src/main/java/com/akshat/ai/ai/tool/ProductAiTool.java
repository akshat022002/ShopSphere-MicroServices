package com.akshat.ai.ai.tool;

import com.akshat.ai.ai.dto.ProductAiResponse;
import com.akshat.ai.catalog.CatalogClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ProductAiTool {

    private final CatalogClient catalogClient;
    private final HttpServletRequest request;

    @Tool(description = """
            Search products available in ShopSphere.
            Use this when user asks to find, compare or recommend ShopSphere products
            """)
    public List<ProductAiResponse> searchProducts(
            @ToolParam(description = "Product name or keyword",required = false)
            String name,

            @ToolParam(description = "Minimum price",required = false)
            BigDecimal minPrice,

            @ToolParam(description = "Maximum price",required = false)
            BigDecimal maxPrice
    ){

        String token = request.getHeader("Authorization");

        return catalogClient.searchProducts(name,minPrice,maxPrice,token);

    }
}
