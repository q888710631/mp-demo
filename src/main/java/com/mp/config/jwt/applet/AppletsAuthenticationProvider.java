package com.mp.config.jwt.applet;

import com.mp.config.jwt.JwtConstants;
import com.mp.config.jwt.TokenProvider;
import com.mp.model.ClientAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class AppletsAuthenticationProvider implements AuthenticationProvider {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // token 验证
        AppletAuthenticationToken appletAuthenticationToken = (AppletAuthenticationToken) authentication;
        String token = (String) appletAuthenticationToken.getCredentials();

        Long clientAccountId = TokenProvider.getClaims(token).get(JwtConstants.LOGIN_KEY, Long.class);
        // 验证clientAccountId
        ClientAccount clientAccount = new ClientAccount();
        clientAccount.setId(1L);

        appletAuthenticationToken.setDetails(clientAccount);
        appletAuthenticationToken.setAccountId(clientAccountId);
        authentication.setAuthenticated(true);
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AppletAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
