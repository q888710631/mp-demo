package com.honyee;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.honyee.config.mybatis.MybatisPlusTenantHandler;
import com.honyee.mapper.RoleMapper;
import com.honyee.mapper.UserMapper;
import com.honyee.model.Role;
import com.honyee.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

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
    void userTest() {
        BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
//        User admin = userMapper.findByUsername("admin");
//        List<Role> roles = roleMapper.findRolesByUserId(admin.getId());
//        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("id", 1L));
//        if (users.isEmpty()) {
//            User entity = new User();
//            entity.setId(1L);
//            entity.setNickname("Honyee");
//            entity.setUsername("admin");
//            entity.setPassword(bc.encode("admin"));
//            userMapper.insert(entity);
//        }
        UUID uuid = UUID.randomUUID();
        String uuidValue = uuid.toString().replaceAll("-", "");
        uuidValue = uuidValue.substring(uuidValue.length() / 4 * 3);
        User entity = new User();
        entity.setNickname("用户" + System.currentTimeMillis());
        entity.setUsername(uuidValue);
        entity.setPassword(bc.encode(uuidValue));
        userMapper.insert(entity);
        log.info(() -> "userTest");
    }

}
