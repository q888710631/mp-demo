package com.mp.config.jwt.applet;

import com.mp.config.jwt.AuthenticationResolver;
import com.mp.config.jwt.JwtConstants;
import com.mp.config.jwt.LoginTypeEnum;
import com.mp.config.jwt.TokenProvider;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppletAuthenticationResolver implements AuthenticationResolver {

    @Override
    public Authentication getAuthentication(String token) {
        Claims claims = TokenProvider.getClaims(token);
        Long accountId = claims.get(JwtConstants.LOGIN_KEY, Long.class);
        AppletAuthenticationToken appletAuthenticationToken = new AppletAuthenticationToken(List.of(), accountId);
        appletAuthenticationToken.setAccountId(accountId);
        appletAuthenticationToken.setAuthenticated(true);
        return appletAuthenticationToken;
    }

    @Override
    public boolean supports(String token) {
        return supports(token, LoginTypeEnum.APPLET);
    }
}
