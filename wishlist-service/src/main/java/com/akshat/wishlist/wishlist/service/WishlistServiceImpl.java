package com.akshat.wishlist.wishlist.service;

import com.akshat.wishlist.catalog.CatalogClient;
import com.akshat.wishlist.catalog.dto.CatalogProduct;
import com.akshat.wishlist.common.exception.DuplicateResourceException;
import com.akshat.wishlist.common.exception.ResourceNotFoundException;
import com.akshat.wishlist.wishlist.dto.WishlistItemResponse;
import com.akshat.wishlist.wishlist.dto.WishlistResponse;
import com.akshat.wishlist.wishlist.entity.Wishlist;
import com.akshat.wishlist.wishlist.entity.WishlistItem;
import com.akshat.wishlist.wishlist.mapper.WishlistItemMapper;
import com.akshat.wishlist.wishlist.repository.WishlistItemRepository;
import com.akshat.wishlist.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Transactional
@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService{

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final CatalogClient catalogClient;
    private final WishlistItemMapper wishlistItemMapper;

    @Transactional(readOnly = true)
    @Override
    public WishlistResponse getWishlist(Long userId, String token) {

        Wishlist wishlist = wishlistRepository.findByUserId(userId).orElse(null);

        if(wishlist==null){
            return new WishlistResponse(null,new ArrayList<>());
        }
        return buildWishlistResponse(wishlist,token);

    }

    @Override
    public WishlistResponse addToWishlist(Long userId, Long productId, String token) {

     //this product object is only to verify if product exists and is active
    CatalogProduct product = catalogClient.getProduct(productId,token);

    Wishlist wishlist = wishlistRepository.findByUserId(userId)
            .orElseGet(()-> wishlistRepository.save(Wishlist.builder()
                            .userId(userId)
                            .build()));

    boolean exists = wishlistItemRepository
            .findByWishlistIdAndProductId(wishlist.getId(),productId).isPresent();

    if(exists){
        throw new DuplicateResourceException("Product already exists in wishlist");
    }

        WishlistItem wishlistItem = WishlistItem.builder()
                .productId(productId)
                .wishlist(wishlist)
                .build();

    wishlist.getItems().add(wishlistItem);

    wishlistRepository.save(wishlist);

    return buildWishlistResponse(wishlist,token);

    }

    @Override
    public void removeFromWishlist(Long userId, Long productId) {

        Wishlist wishlist = getWishlist(userId);

        WishlistItem item = wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(),productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found : "+productId));

        wishlist.getItems().remove(item);

    }

    @Override
    public void clearWishlist(Long userId) {

        Wishlist wishlist = getWishlist(userId);

        wishlist.getItems().clear();
    }

    private Wishlist getWishlist(Long userId){

        return wishlistRepository.findByUserId(userId).orElseThrow(()->
                new ResourceNotFoundException("Wishlist not found for user : "+userId));
    }

    private  WishlistResponse buildWishlistResponse(Wishlist wishlist,String token){

        var items = wishlist.getItems().stream()
                .map(
                        item -> {

                            CatalogProduct product = catalogClient.
                                    getProduct(item.getProductId(),token);

                            WishlistItemResponse response = wishlistItemMapper.toResponse(item);

                            return new WishlistItemResponse(
                                    response.id(),
                                    response.productId(),
                                    product.name(),
                                    product.price());
                        }
                )
                .toList();

        return new WishlistResponse(wishlist.getId(),items);
    }
}
