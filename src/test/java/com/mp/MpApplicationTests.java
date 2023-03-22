package com.mp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mp.config.jwt.JwtConstants;
import com.mp.config.jwt.LoginTypeEnum;
import com.mp.config.jwt.TokenProvider;
import com.mp.config.jwt.my.MyAuthenticationToken;
import com.mp.config.mybatis.MybatisPlusTenantHandler;
import com.mp.dto.CitySimpleDTO;
import com.mp.enums.StateEnum;
import com.mp.mapper.CityMapper;
import com.mp.mapper.ProductMapper;
import com.mp.mapper.RoleMapper;
import com.mp.mapper.UserMapper;
import com.mp.model.City;
import com.mp.model.Product;
import com.mp.model.Role;
import com.mp.model.User;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
