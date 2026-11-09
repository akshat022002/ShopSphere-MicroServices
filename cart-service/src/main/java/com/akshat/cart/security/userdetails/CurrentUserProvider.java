package com.akshat.cart.security.userdetails;

import com.akshat.cart.common.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public Long getUserId(Authentication authentication){
        return (Long) authentication.getPrincipal();
    }

    public Role getUserRole(Authentication authentication){

        String role = authentication.getAuthorities()
                .stream()
                .iterator()
                .next()
                .getAuthority();

        return Role.valueOf(role);
    }



}
