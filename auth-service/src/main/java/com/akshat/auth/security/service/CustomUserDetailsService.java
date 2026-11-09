package com.akshat.auth.security.service;

import com.akshat.auth.common.exception.ResourceNotFoundException;
import com.akshat.auth.security.userdetails.UserDetailsImplementation;
import com.akshat.auth.user.entity.User;
import com.akshat.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(()-> new
                        ResourceNotFoundException("User not found with Email: "+ email));

        return new UserDetailsImplementation(user);

    }
}