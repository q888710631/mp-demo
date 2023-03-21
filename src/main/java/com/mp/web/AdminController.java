package com.mp.web;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mp.dto.RoleDTO;
import com.mp.dto.UserDTO;
import com.mp.dto.UserRoleDTO;
import com.mp.mapper.RoleMapper;
import com.mp.mapper.UserMapper;
import com.mp.service.MyUserDetailService;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Resource
    private MyUserDetailService myUserDetailService;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserMapper userMapper;
    /**
     * 所有角色
     */
    @GetMapping("/all-role")
    public List<RoleDTO> allRoles() {
        return roleMapper.findRoleList();
    }

    /**
     * 用户列表，包含用户拥有的角色
     */
    @GetMapping("/user-list")
    public Page<UserDTO> userList(Pageable pageable) {
        Page<UserDTO> userDTOList = PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize())
            .doSelectPage(() -> userMapper.findUserDTOList());
        List<Long> userIds = userDTOList.stream().map(UserDTO::getId).collect(Collectors.toList());
        List<UserRoleDTO> roles = roleMapper.findRoleKeyByUserIdIn(userIds);
        Map<Long, UserDTO> userMap = userDTOList.stream().collect(Collectors.toMap(UserDTO::getId, Function.identity()));
        for (UserRoleDTO role : roles) {
            userMap.get(role.getUserId()).getRoles().add(role.getRoleKey());
        }
        return userDTOList;
    }

    /**
     * 创建用户
     */
    @PostMapping("create-user")
    public UserDTO createUser(@Validated @RequestBody UserDTO dto) {
        return myUserDetailService.createUser(dto);
    }

    /**
     * 更新用户信息
     */
    @PostMapping("update-user")
    public UserDTO updateUser(@Validated @RequestBody UserDTO dto) {
        return myUserDetailService.updateUser(dto);
    }

    /**
     * 更新用户角色
     */
    @PostMapping("update-role")
    public Collection<RoleDTO> updateUserRole(@Validated @RequestBody UserDTO dto) {
        return myUserDetailService.updateUserRole(dto);
    }
}
