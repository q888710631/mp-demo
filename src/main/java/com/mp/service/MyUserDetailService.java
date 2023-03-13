package com.mp.service;

import com.mp.config.seurity.UserPrincipal;
import com.mp.mapper.RoleMapper;
import com.mp.mapper.UserMapper;
import com.mp.model.Role;
import com.mp.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Resource
    UserMapper userMapper;

    @Resource
    RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findWithRolesByUserName(username);
        if (user != null) {
            return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(Role::getRoleName).map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
        }
        throw new UsernameNotFoundException("账号或密码错误");
    }

    /**
     * 包含roles
     */
    public User findWithRolesByUserName(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        List<Role> roles = roleMapper.findRolesByUserId(user.getId());
        user.setRoles(roles);
        return user;
    }

    public List<Role> findRolesByUserId(Long userId) {
        return roleMapper.findRolesByUserId(userId);
    }

    public List<GrantedAuthority> findGrantedAuthroityByUserId(Long userId) {
        return roleMapper.findRolesByUserId(userId).stream().map(Role::getRoleName).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
