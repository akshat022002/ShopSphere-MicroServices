package com.akshat.order.security.userprovider;

public record AuthenticatedUser(
        Long userId,
        String email
) {
}
