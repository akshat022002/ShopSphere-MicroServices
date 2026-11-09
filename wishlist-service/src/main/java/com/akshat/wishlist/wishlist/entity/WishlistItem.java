package com.akshat.wishlist.wishlist.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wishlist_items",
       uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_wishlist_product",
                columnNames = {"wishlist_id","product_id"}
        )
       },
       indexes = {
        @Index(name = "idx_wishlist_item_wishlist",columnList = "wishlist_id"),
        @Index(name = "idx_wishlist_item_product",columnList = "product_id")
       })
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id",nullable = false)
    private Wishlist wishlist;


    @Column(name = "product_id",nullable = false)
    private Long productId;

}
