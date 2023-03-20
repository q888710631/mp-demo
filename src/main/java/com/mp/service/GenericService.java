package com.mp.service;

import com.mp.config.seurity.AuthenticateMatcher;
import com.mp.config.seurity.SecurityConfiguration;
import com.mp.config.seurity.SecurityConstants;
import com.mp.model.Role;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GenericService {

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
    public  List<String> menusByRoles(Set<String> roles) {
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
}
