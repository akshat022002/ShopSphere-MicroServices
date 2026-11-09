package com.akshat.auth.security.userdetails;


import com.akshat.auth.user.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public UserDetailsImplementation getAuthenticatedUser(Authentication authentication){
        return (UserDetailsImplementation) authentication.getPrincipal();
    }

    public Long getUserId(Authentication authentication){
        return getAuthenticatedUser(authentication).getId();
    }

    public Role getUserRole(Authentication authentication){
        return getAuthenticatedUser(authentication).getRole();
    }
}
