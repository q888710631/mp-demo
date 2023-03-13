package com.mp.config.seurity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.function.Predicate;

public class MyFilterAccessDecisionManager implements AccessDecisionManager {
    private static final Logger log = LoggerFactory.getLogger(MyFilterAccessDecisionManager.class);

    /**
     * 校验角色，能进入此方法的都是需要登录鉴权的
     * @param authentication 当前用户
     * @param configAttributes 需要的角色
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        // 获取认证的用户所具有的角色权限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // 匿名不允许访问，可能是没token、token失效
        if (authorities.stream().map(GrantedAuthority::getAuthority)
            .anyMatch(Predicate.isEqual(SecurityConstants.ROLE_ANONYMOUS))) {
            throw new CredentialsExpiredException("尚未登陆");
        }
        for (ConfigAttribute configAttribute : configAttributes) {
            String attribute = configAttribute.getAttribute();
            if (attribute == null || SecurityConstants.ROLE_ANY.equals(attribute)) {
                return;
            }
            // 轮询判断用户的角色权限是否符合当前资源请求的所需要的权限
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(attribute)) {
                    return;
                }
            }
        }

        throw new AccessForbiddenException("无权限访问");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
