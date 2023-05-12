package com.honyee.app.config.jwt.my;

import com.honyee.app.config.mybatis.MybatisPlusTenantHandler;
import com.honyee.app.service.MyUserDetailService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyAuthenticationProvider implements AuthenticationProvider {

    private final MyUserDetailService myUserDetailService;

    public MyAuthenticationProvider(MyUserDetailService myUserDetailService) {
        this.myUserDetailService = myUserDetailService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // token 验证
        MyAuthenticationToken auth = (MyAuthenticationToken) authentication;
        Long userId = auth.getUserId();
        List<GrantedAuthority> authorities = myUserDetailService.findGrantedAuthorityByUserId(userId);
        MybatisPlusTenantHandler.setTenantValue(userId);
        return new MyAuthenticationToken(userId, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MyAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
