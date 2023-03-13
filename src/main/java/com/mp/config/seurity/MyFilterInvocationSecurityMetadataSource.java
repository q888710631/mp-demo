package com.mp.config.seurity;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MyFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private static final Logger log = LoggerFactory.getLogger(MyFilterInvocationSecurityMetadataSource.class);

    public List<AuthenticateMatcher> matchers;

    private final FilterInvocationSecurityMetadataSource securityMetadataSource;

    public MyFilterInvocationSecurityMetadataSource(FilterInvocationSecurityMetadataSource securityMetadataSource) {
        this.securityMetadataSource = securityMetadataSource;
        try {
            initMatchers();
        } catch (IOException e) {
            log.error("权限文件初始化失败：{}", e.getMessage());
        }
    }

    private void initMatchers() throws IOException {
        ClassPathResource application = new ClassPathResource("authentication.yml");
        InputStream inputStream = new FileInputStream(application.getFile());
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        Object authenticateMatchers = data.get("authenticate-matchers");
        List<AuthenticateMatcher> matchers = new ArrayList<>();
        if (authenticateMatchers != null) {
            for (Object authenticateMatcher : (Collection) authenticateMatchers) {
                Map map = (Map) authenticateMatcher;
                AuthenticateMatcher matcher = new AuthenticateMatcher();
                matchers.add(matcher);
                // pathMatchers
                Object pathMatchers = map.get("path-matchers");
                if (pathMatchers == null) {
                    matcher.setPathMatchers(List.of());
                } else if (pathMatchers instanceof String) {
                    matcher.setPathMatchers(List.of(pathMatchers.toString()));
                } else {
                    matcher.setPathMatchers((List) pathMatchers);
                }

                // has-any-role
                Object hasAnyRole = map.get("has-any-role");
                if (hasAnyRole == null) {
                    matcher.setHasAnyRole(List.of());
                } else if (hasAnyRole instanceof String) {
                    matcher.setHasAnyRole(List.of(hasAnyRole.toString()));
                } else {
                    matcher.setHasAnyRole((List) hasAnyRole);
                }
                // authenticated
                matcher.setAuthenticated((Boolean) map.get("authenticated"));
            }
        }
        this.matchers = matchers;
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
        String requestUrl = filterInvocation.getRequestUrl();
        String method = request.getMethod();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (AuthenticateMatcher authenticateMatcher : matchers) {
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
