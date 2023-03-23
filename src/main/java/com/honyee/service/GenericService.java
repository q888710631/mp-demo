package com.honyee.service;

import com.honyee.config.seurity.AuthenticateMatcher;
import com.honyee.config.seurity.SecurityConfiguration;
import com.honyee.config.seurity.SecurityConstants;
import com.honyee.model.Role;
import com.honyee.utils.LogUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GenericService implements InitializingBean {
    @Value("${server.port}")
    private String port;

    @Resource
    private MyUserDetailService myUserDetailService;

    @Cacheable(value = "user-menu", key = "#userId", cacheManager = "memoryCacheManager")
    public List<String> userMenu(Long userId) {
        List<Role> roles = myUserDetailService.findRolesByUserId(userId);
        Set<String> roleKeys = roles.stream().map(Role::getRoleKey).collect(Collectors.toSet());
        return menusByRoles(roleKeys);
    }

    /**
     * 获取角色可用的菜单
     */
    public List<String> menusByRoles(Set<String> roles) {
        Set<String> formatRoles = roles.stream().map(SecurityConstants::roleFormat).collect(Collectors.toSet());

        return SecurityConfiguration.authenticateProperties.getMenuMatchers().stream().filter(matcher -> {
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

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtil.get().info("apifox导入地址：http://localhost:{}/v3/api-docs", port);
    }
}
