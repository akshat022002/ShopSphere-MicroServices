package com.akshat.wishlist.wishlist.mapper;

import com.akshat.wishlist.wishlist.dto.WishlistItemResponse;
import com.akshat.wishlist.wishlist.entity.WishlistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WishlistItemMapper {

    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "price", ignore = true)
    WishlistItemResponse toResponse(WishlistItem wishlistItem);
}