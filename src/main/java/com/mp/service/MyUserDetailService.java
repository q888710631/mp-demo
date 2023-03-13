package com.mp.service;

import com.mp.config.seurity.UserPrincipal;
import com.mp.model.Role;
import com.mp.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUserByUserName(username);
        if (user != null) {
            return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(Role::getName).map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
        }
        throw new UsernameNotFoundException("账号或密码错误");
    }

    // 从数据库查询
    public User findUserByUserName(String username) {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setPassword("$2a$10$9fbei2..mD4NOSZ2XyMOTeiwYK2nG6b6LS2Qt5esmOWozdg5AtiP6");
        user.setRoles(findRolesByUserId(user.getId()));
        return user;
    }

    public List<Role> findRolesByUserId(Long userId) {
        Role role = new Role();
        role.setId(1L);
        role.setName("admin");
        return List.of(role);
    }

    public List<GrantedAuthority> findGrantedAuthroityByUserId(Long userId) {
        return findRolesByUserId(userId).stream().map(Role::getName).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
