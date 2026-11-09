package com.akshat.order.security.userprovider;

import com.akshat.order.common.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public Long getUserId(Authentication authentication){

      AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
      return user.userId();

    }

    public String getUserEmail(Authentication authentication){

        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return user.email();
    }

    public Role getUserRole(Authentication authentication){

        String role= authentication
                .getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        return Role.valueOf(role);

    }

}
