package com.mp.web;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mp.config.TenantHelper;
import com.mp.config.seurity.AuthenticationConstants;
import com.mp.dto.CitySimpleDTO;
import com.mp.dto.RoleDTO;
import com.mp.dto.UserDTO;
import com.mp.dto.UserRoleDTO;
import com.mp.mapper.RoleMapper;
import com.mp.mapper.UserMapper;
import com.mp.mapperstruct.UserMapperStruct;
import com.mp.model.Role;
import com.mp.model.User;
import com.mp.service.CacheService;
import com.mp.service.MyUserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GenericController {
    private final Logger log = LoggerFactory.getLogger(GenericController.class);

    @Autowired
    private CacheService cacheService;

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/test")
    public void testGet(String abc, HttpServletResponse response) throws IOException {
        cacheService.cacheRedis("123");
        cacheService.cacheMemory("123");

        PrintWriter writer = response.getWriter();
        writer.write("close");
        writer.flush();
        writer.close();
        System.out.println("最终");
    }

    @Resource
    UserMapperStruct userMapperStruct;

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody Object obj) {
        CitySimpleDTO citySimpleDTO = new CitySimpleDTO();
        citySimpleDTO.setName("abc");
        User user = new User();
        user.setId(1L);
        user.setNickname("abc");
        user.setUsername("admin");

        return ResponseEntity.ok(userMapperStruct.toDto(user));
    }

    /**
     * 当前用户可用菜单
     */
    @GetMapping("/user-menu")
    public List<String> userMenu() {
        List<Role> roles = myUserDetailService.findRolesByUserId(TenantHelper.getTenantId());
        Set<String> roleKeys = roles.stream().map(Role::getRoleKey).collect(Collectors.toSet());
        return AuthenticationConstants.menusByRoles(roleKeys);
    }

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
