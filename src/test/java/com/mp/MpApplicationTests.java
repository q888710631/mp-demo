package com.mp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mp.config.mybatis.MybatisPlusTenantHandler;
import com.mp.mapper.RoleMapper;
import com.mp.mapper.UserMapper;
import com.mp.model.Role;
import com.mp.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
//@InterceptorIgnore(tenantLine = "true")
class MpApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(MpApplicationTests.class);

    @Resource
    UserMapper userMapper;

    @Resource
    RoleMapper roleMapper;

    @BeforeAll
    public static void beforeAll() {
        MybatisPlusTenantHandler.setTenantValue(1L);
        System.out.println("### 租户注入");
    }

    @Test
    public void emptyInit() {
        log.info(() -> "emptyInit");
    }

    @Test
    void queryUser() {
        User admin = userMapper.findByUsername("admin");
        List<Role> roles = roleMapper.findRolesByUserId(admin.getId());
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("id", 1L));
        if (users.isEmpty()) {
            User entity = new User();
            entity.setId(1L);
            entity.setNickname("Honyee");
            userMapper.insert(entity);
        }
        log.info(() -> "queryUser");
    }

}
