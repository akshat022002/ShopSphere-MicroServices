package com.akshat.auth.security.redis;

public interface TokenBlacklistService {

    void blacklistToken(String token);

    boolean isBlacklisted(String token);
}
