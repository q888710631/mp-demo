package com.honyee.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.honyee.app.config.seurity.SecurityConstants;
import com.honyee.app.dto.*;
import com.honyee.app.enums.UserStateEnum;
import com.honyee.app.exp.CommonException;
import com.honyee.app.exp.DataNotExistsException;
import com.honyee.app.mapper.RoleMapper;
import com.honyee.app.mapper.UserMapper;
import com.honyee.app.mapper.UserRoleMapper;
import com.honyee.app.mapperstruct.RoleMapperStruct;
import com.honyee.app.mapperstruct.UserMapperStruct;
import com.honyee.app.model.Role;
import com.honyee.app.model.User;
import com.honyee.app.model.UserRole;
import com.honyee.app.utils.TenantHelper;
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
import java.time.LocalDateTime;
import java.util.*;
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
        return findWithRolesByUserName(username);
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
    public List<GrantedAuthority> findGrantedAuthorityByUserId(Long userId) {
        return roleMapper.findRolesByUserId(userId).stream()
                .map(Role::getRoleKey)
                .map(SecurityConstants::roleFormat)
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    /**
     * 创建用户
     */
    @Transactional
    public UserDTO createUser(UserCreateDTO dto) {
        String nickname = dto.getNickname();
        String username = dto.getUsername();
        String password = dto.getPassword();
        Long countNickname = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getNickname, nickname));
        if (countNickname != null && countNickname > 0) {
            throw new CommonException("昵称已经存在");
        }
        Long countUsername = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (countUsername != null && countUsername > 0) {
            throw new CommonException("用户名已经存在");
        }
        UserStateEnum state = dto.getState();
        state = state == null ? UserStateEnum.ENABLE : UserStateEnum.DISABLE;
        User user = new User();
        user.setNickname(username);
        user.setUsername(username);
        user.setPassword(bc.encode(password));
        user.setState(state);
        userMapper.insert(user);
        return userMapperStruct.toDto(user);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserDTO updateUser(UserUpdateDTO dto) {
        Long userId = dto.getId();
        String nickname = dto.getNickname();
        String username = dto.getUsername();
        String password = dto.getPassword();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new DataNotExistsException(userId);
        }
        if (StringUtils.isNotBlank(nickname)) {
            Long countNickname = userMapper.selectCount(new LambdaQueryWrapper<User>().ne(User::getId, userId).eq(User::getNickname, nickname));
            if (countNickname != null && countNickname > 0) {
                throw new CommonException("昵称已经存在");
            }
            user.setNickname(username);
        }

        if (StringUtils.isNotBlank(nickname)) {
            Long countUsername = userMapper.selectCount(new LambdaQueryWrapper<User>().ne(User::getId, userId).eq(User::getUsername, username));
            if (countUsername != null && countUsername > 0) {
                throw new CommonException("用户名已经存在");
            }
            user.setUsername(username);
        }
        if (StringUtils.isNotBlank(password)) {
            user.setPassword(bc.encode(password));
        }
        UserStateEnum state = dto.getState();
        if (state != null && Set.of(UserStateEnum.ENABLE, UserStateEnum.DISABLE).contains(state)) {
            user.setState(state);
        }

        userMapper.updateById(user);
        return userMapperStruct.toDto(user);
    }

    /**
     * 更新用户角色
     */
    @Transactional
    public Collection<RoleDTO> updateUserRole(UserRoleUpdateDTO dto) {
        Long userId = dto.getId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new DataNotExistsException(userId);
        }

        Long currentUserId = TenantHelper.getCurrentUserId();
        if (Objects.equals(currentUserId, userId)) {
            throw new CommonException("不能修改自身角色");
        }
        // 当前用户的最大角色级别
        Integer userTopRoleLevel = userRoleMapper.findUserTopRoleLevel(currentUserId);
        if (userTopRoleLevel == null) {
            userTopRoleLevel = Integer.MAX_VALUE;
        }
        final int level = userTopRoleLevel;
        List<Role> roles = roleMapper.selectList(new LambdaQueryWrapper<Role>().in(Role::getRoleKey, dto.getRoleKeyList()));
        // 过滤不大于自身级别的角色
        List<String> roleKeyList = roles.stream()
            .filter(role -> role.getRoleLevel() > level)
            .map(Role::getRoleKey)
            .collect(Collectors.toList());

        if (roleKeyList.isEmpty()) {
            return List.of();
        }

        Map<String, Role> resultRoleKeyMap = roleMapper.selectList(new LambdaQueryWrapper<Role>().in(Role::getRoleKey, roleKeyList)).stream().collect(Collectors.toMap(Role::getRoleKey, Function.identity()));
        Map<String, Role> hasRoleKeyMap = roleMapper.findRolesByUserId(userId).stream().collect(Collectors.toMap(Role::getRoleKey, Function.identity()));
        // 需要新增的
        for (Role role : resultRoleKeyMap.values()) {
            String roleKey = role.getRoleKey();
            if (hasRoleKeyMap.get(roleKey) == null) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(role.getId());
                userRoleMapper.insert(userRole);
            }
        }
        // 需要删除的
        for (Role role : hasRoleKeyMap.values()) {
            String roleKey = role.getRoleKey();
            if (resultRoleKeyMap.get(roleKey) == null) {
                userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId).eq(UserRole::getRoleId, role.getId()));
            }
        }

        return roleMapperStruct.toDto(new ArrayList<>(resultRoleKeyMap.values()));
    }

    /**
     * 更新用户状态和锁定时间
     */
    @Transactional
    public UserDTO updateUserStateOrLock(UpdateUserStateOrLockDTO dto) {
        Long userId = dto.getId();
        UserStateEnum state = dto.getState();
        Boolean unlock = dto.getUnlock();
        LocalDateTime lockBeginDate = dto.getLockBeginDate();
        LocalDateTime lockEndDate = dto.getLockEndDate();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new DataNotExistsException(userId);
        }
        Long currentUserId = TenantHelper.getCurrentUserId();
        if (Objects.equals(currentUserId, userId) && state == UserStateEnum.DISABLE) {
            throw new CommonException("不能操作自身");
        }
        if (state != null) {
            user.setState(state);
        }

        if (Boolean.TRUE.equals(unlock)) {
            user.setLockBeginDate(null);
            user.setLockEndDate(null);
        } else if (Boolean.FALSE.equals(unlock)) {
            if (lockBeginDate == null || lockEndDate == null || lockBeginDate.isAfter(lockEndDate)) {
                throw new CommonException("锁定区间不正确");
            }
            user.setLockBeginDate(lockBeginDate);
            user.setLockEndDate(lockEndDate);
        }
        userMapper.updateById(user);
        return userMapperStruct.toDto(user);
    }

}
