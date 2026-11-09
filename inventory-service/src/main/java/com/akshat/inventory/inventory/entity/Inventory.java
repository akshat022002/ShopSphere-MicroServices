package com.akshat.inventory.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long productId;

    @Builder.Default
    @Column(nullable = false)
    private Integer stockQuantity=0;

    @Version
    private Long version;
}