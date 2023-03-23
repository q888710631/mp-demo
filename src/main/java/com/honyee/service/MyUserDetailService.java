package com.honyee.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.honyee.config.seurity.SecurityConstants;
import com.honyee.config.seurity.UserPrincipal;
import com.honyee.dto.RoleDTO;
import com.honyee.dto.UserDTO;
import com.honyee.exp.CommonException;
import com.honyee.exp.DataNotExistsException;
import com.honyee.mapper.RoleMapper;
import com.honyee.mapper.UserMapper;
import com.honyee.mapper.UserRoleMapper;
import com.honyee.mapperstruct.RoleMapperStruct;
import com.honyee.mapperstruct.UserMapperStruct;
import com.honyee.model.Role;
import com.honyee.model.User;
import com.honyee.model.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Resource
    UserMapper userMapper;

    @Resource
    RoleMapper roleMapper;

    @Resource
    UserMapperStruct userMapperStruct;

    @Resource
    RoleMapperStruct roleMapperStruct;

    @Resource
    UserRoleMapper userRoleMapper;

    private static final BCryptPasswordEncoder bc = new BCryptPasswordEncoder();

    @Cacheable(value = "user-login", key = "#username", cacheManager = "memoryCacheManager")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findWithRolesByUserName(username);
        if (user == null) {
            return new UserPrincipal(null, null, null, null);
        }
        return new UserPrincipal(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            user.getRoles().stream().map(Role::getRoleName).map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );
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

    /**
     * 解析token时查询角色，内存中短时间缓存减少数据库查询
     */
    @Cacheable(value = "user-role", key = "#userId", cacheManager = "memoryCacheManager")
    public List<GrantedAuthority> findGrantedAuthroityByUserId(Long userId) {
        return roleMapper.findRolesByUserId(userId).stream()
            .map(Role::getRoleKey)
            .map(SecurityConstants::roleFormat)
            .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    /**
     * 创建用户
     */
    @Transactional
    public UserDTO createUser(UserDTO dto) {
        Long userId = dto.getId();
        String nickname = dto.getNickname();
        String username = dto.getUsername();
        String password = dto.getPassword();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new DataNotExistsException(userId);
        }
        Long countNickname = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getNickname, nickname));
        if (countNickname != null && countNickname > 0) {
            throw new CommonException("昵称已经存在");
        }
        user.setNickname(username);
        Long countUsername = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (countUsername != null && countUsername > 0) {
            throw new CommonException("用户名已经存在");
        }
        user.setUsername(username);
        user.setPassword(bc.encode(password));
        userMapper.insert(user);
        return userMapperStruct.toDto(user);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserDTO updateUser(UserDTO dto) {
        Long id = dto.getId();
        String nickname = dto.getNickname();
        String username = dto.getUsername();
        String password = dto.getPassword();
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new DataNotExistsException(id);
        }
        if (StringUtils.isNotBlank(nickname)) {
            Long countNickname = userMapper.selectCount(new LambdaQueryWrapper<User>().ne(User::getId, id).eq(User::getNickname, nickname));
            if (countNickname != null && countNickname > 0) {
                throw new CommonException("昵称已经存在");
            }
            user.setNickname(username);
        }

        if (StringUtils.isNotBlank(nickname)) {
            Long countUsername = userMapper.selectCount(new LambdaQueryWrapper<User>().ne(User::getId, id).eq(User::getUsername, username));
            if (countUsername != null && countUsername > 0) {
                throw new CommonException("用户名已经存在");
            }
            user.setUsername(username);
        }
        if (StringUtils.isNotBlank(nickname)) {
            user.setPassword(bc.encode(password));
        }
        userMapper.updateById(user);
        return userMapperStruct.toDto(user);
    }

    /**
     * 更新用户角色
     */
    @Transactional
    public Collection<RoleDTO> updateUserRole(UserDTO dto) {
        Long userId = dto.getId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new DataNotExistsException(userId);
        }
        Collection<String> roleKeyList = dto.getRoles();
        if (roleKeyList.isEmpty()) {
            userRoleMapper.delete(new LambdaUpdateWrapper<UserRole>().eq(UserRole::getUserId, userId));
            return List.of();
        }

        Map<String, Role> resultRoleKeyMap = roleMapper.selectList(new LambdaQueryWrapper<Role>().in(Role::getRoleKey, roleKeyList)).stream().collect(Collectors.toMap(Role::getRoleKey, Function.identity()));
        Map<String, Role> hasRoleKeyMap = roleMapper.findRolesByUserId(userId).stream().collect(Collectors.toMap(Role::getRoleKey, Function.identity()));
        // 需要新增的
        for (Role role : resultRoleKeyMap.values()) {
            if (hasRoleKeyMap.get(role.getRoleKey()) == null) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(role.getId());
                userRoleMapper.insert(userRole);
            }
        }
        // 需要删除的
        for (Role role : hasRoleKeyMap.values()) {
            if (resultRoleKeyMap.get(role.getRoleKey()) == null) {
                userRoleMapper.deleteByUserIdAndRoleId(userId, role.getId());
            }
        }

        return roleMapperStruct.toDto(new ArrayList<>(resultRoleKeyMap.values()));
    }

}
