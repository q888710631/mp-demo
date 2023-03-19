package com.mp.service;

import com.mp.config.seurity.AuthenticationConstants;
import com.mp.model.Role;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
        return AuthenticationConstants.menusByRoles(roleKeys);
    }
}
