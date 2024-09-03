package com.honyee.app.web;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.honyee.app.config.http.MyResponse;
import com.honyee.app.dto.*;
import com.honyee.app.mapper.RoleMapper;
import com.honyee.app.mapper.UserMapper;
import com.honyee.app.service.MyUserDetailService;
import com.honyee.app.utils.SpringUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Resource
    private MyUserDetailService myUserDetailService;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserMapper userMapper;

    @Operation(summary = "所有角色")
    @GetMapping("/all-role")
    public List<RoleDTO> allRoles() {
        return roleMapper.findRoleList();
    }

    @Operation(summary = "用户列表，包含用户拥有的角色")
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

    @Operation(summary = "创建用户")
    @PostMapping("create-user")
    public UserDTO createUser(@Validated @RequestBody UserCreateDTO dto) {
        return myUserDetailService.createUser(dto);
    }

    @Operation(summary = "更新用户信息")
    @PostMapping("update-user")
    public UserDTO updateUser(@Validated @RequestBody UserUpdateDTO dto) {
        return myUserDetailService.updateUser(dto);
    }

    @Operation(summary = "更新用户角色")
    @PostMapping("update-role")
    public Collection<RoleDTO> updateUserRole(@Validated @RequestBody UserRoleUpdateDTO dto) {
        return myUserDetailService.updateUserRole(dto);
    }

    @Operation(summary = "更新用户状态和锁定时间")
    @PostMapping("update-user-state-or-lock")
    public UserDTO updateUserStateOrLock(@Validated @RequestBody UpdateUserStateOrLockDTO dto) {
        return myUserDetailService.updateUserStateOrLock(dto);
    }

    @Operation(summary = "手动关闭服务")
    //@PostMapping("close-server")
    public MyResponse<Void> closeServer() {
        new Thread(() -> {
            log.info("准备关闭服务");
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            SpringApplication.exit(SpringUtil.CONTEXT);
        }).start();
        return new MyResponse<>();
    }

}
