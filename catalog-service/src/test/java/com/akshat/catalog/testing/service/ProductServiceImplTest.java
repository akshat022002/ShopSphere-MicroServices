package com.akshat.catalog.testing.service;

import com.akshat.catalog.common.enums.Role;
import com.akshat.catalog.common.exception.AccessDeniedException;
import com.akshat.catalog.common.exception.ResourceNotFoundException;
import com.akshat.catalog.product.dto.ProductResponse;
import com.akshat.catalog.product.dto.ProductUpdateRequest;
import com.akshat.catalog.product.entity.Product;
import com.akshat.catalog.product.mapper.ProductMapper;
import com.akshat.catalog.product.repository.ProductRepository;
import com.akshat.catalog.product.service.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @ParameterizedTest
    @ValueSource(longs = {0,2,10})
    public void shouldReturnActiveProductWhenGetProductById(Long productId){

        //ARRANGE
        //Long productId;

        Product product = mock(Product.class);
        ProductResponse expectedResponse = mock(ProductResponse.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        when(product.isActive()).thenReturn(true);

        when(productMapper.toResponse(product)).thenReturn(expectedResponse);

        //ACT
        ProductResponse actualResponse = productService.getProductById(productId);

        //ASSERT
        assertSame(expectedResponse,actualResponse);

        verify(productRepository).findById(productId);
        verify(productMapper).toResponse(product);

    }

    @ParameterizedTest
    @ValueSource(longs = {1L,5L,6L})
    public void shouldThrowExceptionWhenProductDoesNotExist(Long productId){

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                ()->productService.getProductById(productId));

        verify(productRepository).findById(productId);


    }

    @Test
    void shouldUpdateProductWhenSellerIsOwner(){

        Long productId = 1L;
        Long userId = 5L;

        Product product = mock(Product.class);
        ProductUpdateRequest request = mock(ProductUpdateRequest.class);
        ProductResponse expectedRes = mock(ProductResponse.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(product.isActive()).thenReturn(true);
        when(product.getSellerId()).thenReturn(userId);

        when(request.name()).thenReturn("Update Laptop");
        when(request.description()).thenReturn(null);
        when(request.price()).thenReturn(null);

        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(expectedRes);


        ProductResponse actualResponse = productService.updateProduct(productId,userId,request, Role.ROLE_CUSTOMER);

        assertSame(expectedRes,actualResponse);

    }

    @Test
    void shouldThrowExceptionWhenSellerDoesNotOwnProduct(){

        Long userId = 1L;
        Long productId = 2L;
        Long sellerId = 5L;

        Product product = mock(Product.class);
        ProductUpdateRequest request = mock(ProductUpdateRequest.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(product.isActive()).thenReturn(true);
        when(product.getSellerId()).thenReturn(sellerId);

        assertThrows(AccessDeniedException.class,
                ()-> productService.updateProduct(productId,userId,request,Role.ROLE_SELLER));

        verify(productRepository,never()).save(any(Product.class));

    }



}
