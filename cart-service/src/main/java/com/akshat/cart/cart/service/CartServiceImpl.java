package com.akshat.cart.cart.service;

import com.akshat.cart.catalog.CatalogClient;
import com.akshat.cart.catalog.dto.CatalogProduct;
import com.akshat.cart.cart.dto.AddToCartRequest;
import com.akshat.cart.cart.dto.CartItemResponse;
import com.akshat.cart.cart.dto.CartResponse;
import com.akshat.cart.cart.dto.UpdateCartItemRequest;
import com.akshat.cart.cart.entity.Cart;
import com.akshat.cart.cart.entity.CartItem;
import com.akshat.cart.cart.mapper.CartItemMapper;
import com.akshat.cart.cart.repository.CartItemRepository;
import com.akshat.cart.cart.repository.CartRepository;
import com.akshat.cart.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{

    private final CatalogClient catalogClient;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final CartItemMapper cartItemMapper;

    @Transactional(readOnly = true)
    @Override
    public CartResponse getCart(Long userId, String token) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(()-> new
                        ResourceNotFoundException("Cart not found for user : "+userId));

        return buildCartResponse(cart,token);

    }

    @Override
    public CartResponse addToCart(Long userId, Long productId, AddToCartRequest request, String token) {

        CatalogProduct product = catalogClient.getProduct(productId,token);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(()->
                        cartRepository.save(Cart.builder()
                                .userId(userId)
                                .build())
                );

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(),product.id())
                .orElse(null);

        if(cartItem==null){
            cartItem=CartItem.builder()
                    .cart(cart)
                    .quantity(request.quantity())
                    .productId(productId)
                    .build();

            cart.getItems().add(cartItem);
        } else {

            cartItem.setQuantity(cartItem.getQuantity()+request.quantity());
        }

        cartRepository.save(cart);

        return buildCartResponse(cart,token);
    }

    @Override
    public CartResponse updateCartItem(Long userId, Long productId, UpdateCartItemRequest request, String token) {

        Cart cart = getCartEntity(userId);

       CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(),productId)
               .orElseThrow(()-> new ResourceNotFoundException("Cart item not found for product : "+productId));

       cartItem.setQuantity(request.quantity());

       return  buildCartResponse(cart,token);

    }

    @Override
    public void removeCartItem(Long userId, Long productId) {

        Cart cart = getCartEntity(userId);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart item not found for product : "+productId));

        cart.getItems().remove(cartItem);

    }

    @Override
    public void clearCart(Long userId) {

        Cart cart = getCartEntity(userId);

        cart.getItems().clear();

    }

    private CartResponse buildCartResponse(Cart cart, String token){

        var items = cart.getItems().stream()
                .map( item-> {

                    CatalogProduct product = catalogClient.getProduct(item.getProductId(),token);

                    CartItemResponse response = cartItemMapper.toResponse(item);

                    BigDecimal subtotal = product.price().multiply(BigDecimal.valueOf(item.getQuantity()));

                    return new CartItemResponse(
                            response.id(),
                            item.getProductId(),
                            product.name(),
                            product.price(),
                            item.getQuantity(),
                            subtotal);

                } ).toList();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

    return new CartResponse(cart.getId(), items,totalAmount);

    }

    private Cart getCartEntity(Long userId){

        return cartRepository.findByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart not found for user : "+userId));
    }
}
