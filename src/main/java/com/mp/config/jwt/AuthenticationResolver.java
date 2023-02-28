package com.mp.config.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;

/**
 * 解析touken
 */
public interface AuthenticationResolver {

    Authentication getAuthentication(String token);

    /**
     * 支持的token类型（login-type)
     */
    boolean supports(String token);

    default boolean supports(String token, LoginTypeEnum loginType) {
        Claims claims = TokenProvider.getClaims(token);
        String s = claims.get(JwtConstants.LOGIN_TYPE, String.class);
        return loginType.name().equals(s);
    }
}
