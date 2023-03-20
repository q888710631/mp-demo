package com.mp.config.seurity;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

public class MyFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private static final Logger log = LoggerFactory.getLogger(MyFilterInvocationSecurityMetadataSource.class);

    private final FilterInvocationSecurityMetadataSource securityMetadataSource;

    private final AntPathMatcher antPathMatcher;

    // 权限配置
    private final AuthenticateProperties authenticateProperties;

    public MyFilterInvocationSecurityMetadataSource(FilterInvocationSecurityMetadataSource securityMetadataSource, AuthenticateProperties authenticateProperties) {
        this.securityMetadataSource = securityMetadataSource;
        this.antPathMatcher = new AntPathMatcher();
        this.authenticateProperties = authenticateProperties;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    // 获取当前url对应的权限
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) {
        if (!(object instanceof FilterInvocation)) {
            return securityMetadataSource.getAllConfigAttributes();
        }

        FilterInvocation filterInvocation = (FilterInvocation) object;
        final HttpServletRequest request = filterInvocation.getRequest();
        String requestUrl = filterInvocation.getRequest().getRequestURI();
        String method = request.getMethod();
        for (AuthenticateMatcher authenticateMatcher : authenticateProperties.getAuthenticateMatchers()) {
            // method不匹配
            if (authenticateMatcher.getHttpMethod() != null && !authenticateMatcher.getHttpMethod().matches(method)) {
                continue;
            }
            // 路径匹配
            List<String> pathMatchers = authenticateMatcher.getPathMatchers();
            if (pathMatchers.stream().noneMatch(pattern -> antPathMatcher.match(pattern, requestUrl))) {
                continue;
            }
            // 需要鉴权
            if (!Boolean.FALSE.equals(authenticateMatcher.getAuthenticated())) {
                // 匹配角色
                if (!ObjectUtils.isEmpty(authenticateMatcher.getHasAnyRole())) {
                    String[] roles = authenticateMatcher.getHasAnyRole().stream().map(SecurityConstants::roleFormat)
                        .filter(StringUtils::isNotBlank).toArray(String[]::new);
                    return SecurityConfig.createList(roles);
                }
                return SecurityConfig.createList(SecurityConstants.ROLE_ANY);
            }
            // 不需要鉴权
            return null;
        }

        // 返回代码定义的默认配置
        return securityMetadataSource.getAllConfigAttributes();
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
