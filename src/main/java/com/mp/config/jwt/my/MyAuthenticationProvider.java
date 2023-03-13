package com.mp.config.jwt.my;

import com.mp.config.jwt.JwtConstants;
import com.mp.config.jwt.TokenProvider;
import com.mp.config.mybatis.MybatisPlusTenantHandler;
import com.mp.model.ClientAccount;
import com.mp.service.MyUserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyAuthenticationProvider implements AuthenticationProvider {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // token 验证
        MyAuthenticationToken auth = (MyAuthenticationToken) authentication;
        Long userId = auth.getUserId();
        List<GrantedAuthority> authorities = myUserDetailService.findGrantedAuthroityByUserId(userId);
        MybatisPlusTenantHandler.setTenantValue(userId);
        return new MyAuthenticationToken(userId, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MyAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
