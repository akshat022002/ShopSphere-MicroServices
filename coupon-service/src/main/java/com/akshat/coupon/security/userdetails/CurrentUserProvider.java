package com.akshat.coupon.security.userdetails;

import com.akshat.coupon.common.enums.Role;
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
