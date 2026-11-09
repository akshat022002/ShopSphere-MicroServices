package com.akshat.auth.user.service;

import com.akshat.auth.dashboard.dto.AdminUserStatistics;
import com.akshat.auth.user.enums.Role;
import com.akshat.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public AdminUserStatistics getUserCount() {

        long totalUsers = userRepository.countByDeletedFalse();
        long totalSellers = userRepository.countByRoleAndDeletedFalse(Role.ROLE_SELLER);
        long totalCustomers = userRepository.countByRoleAndDeletedFalse(Role.ROLE_CUSTOMER);

        return new AdminUserStatistics(
                totalUsers,
                totalSellers,
                totalCustomers);

    }
}
