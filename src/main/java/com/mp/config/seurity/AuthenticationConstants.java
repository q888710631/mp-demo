package com.mp.config.seurity;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class AuthenticationConstants {
    private static final Logger log = LoggerFactory.getLogger(MyFilterInvocationSecurityMetadataSource.class);

    // url权限配置
    public static List<AuthenticateMatcher> AUTHENTICATE_MATCHERS;

    // menu权限配置
    public static List<AuthenticateMatcher> MENU_MATCHERS;

    static {
        try {
            AUTHENTICATE_MATCHERS = initMatchers("authenticate-matchers");
            MENU_MATCHERS = initMatchers("menu-matchers");
        } catch (IOException e) {
            log.error("权限文件初始化失败：{}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static List<AuthenticateMatcher> initMatchers(String parentKey) throws IOException {
        ClassPathResource application = new ClassPathResource("authentication.yml");
        InputStream inputStream = new FileInputStream(application.getFile());
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        Object authenticateMatchers = data.get(parentKey);
        List<AuthenticateMatcher> matchers = new ArrayList<>();
        if (authenticateMatchers != null) {
            for (Object authenticateMatcher : (Collection<?>) authenticateMatchers) {
                Map<String, ?> map = (Map<String, ?>) authenticateMatcher;
                AuthenticateMatcher matcher = new AuthenticateMatcher();
                matchers.add(matcher);
                // pathMatchers
                Object pathMatchers = map.get("path-matchers");
                if (pathMatchers == null) {
                    matcher.setPathMatchers(List.of());
                } else if (pathMatchers instanceof String) {
                    matcher.setPathMatchers(List.of(pathMatchers.toString()));
                } else {
                    matcher.setPathMatchers((List<String>) pathMatchers);
                }

                // has-any-role
                Object hasAnyRole = map.get("has-any-role");
                if (hasAnyRole == null) {
                    matcher.setHasAnyRole(List.of());
                } else if (hasAnyRole instanceof String) {
                    matcher.setHasAnyRole(List.of(hasAnyRole.toString()));
                } else {
                    matcher.setHasAnyRole((List<String>) hasAnyRole);
                }
                // authenticated
                matcher.setAuthenticated((Boolean) map.get("authenticated"));
            }
        }
        return matchers;
    }

    /**
     * 获取角色可用的菜单
     */
    public static List<String> menusByRoles(Set<String> roles) {
        Set<String> formatRoles = roles.stream().map(SecurityConstants::roleFormat).collect(Collectors.toSet());

        return MENU_MATCHERS.stream().filter(matcher -> {
                if (ObjectUtils.isEmpty(matcher.getPathMatchers())) {
                    return false;
                }
                if (Boolean.FALSE.equals(matcher.getAuthenticated())
                    || ObjectUtils.isEmpty(matcher.getHasAnyRole())) {
                    return true;
                }
                List<String> hasAnyRole = matcher.getHasAnyRole();
                return formatRoles.retainAll(hasAnyRole);
            })
            .map(AuthenticateMatcher::getPathMatchers)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

}
