package com.honyee.app;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.rholder.retry.*;
import com.honyee.app.config.mybatis.MybatisPlusTenantHandler;
import com.honyee.app.mapper.RoleMapper;
import com.honyee.app.mapper.UserMapper;
import com.honyee.app.model.Role;
import com.honyee.app.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@SpringBootTest
//@InterceptorIgnore(tenantLine = "true")
class MyApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(MyApplicationTests.class);

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
        User admin = userMapper.findByUsername("admin");
        List<Role> roles = roleMapper.findRolesByUserId(admin.getId());
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("id", 1L));
        if (users.isEmpty()) {
            User entity = new User();
            entity.setId(1L);
            entity.setNickname("Honyee");
            entity.setUsername("admin");
            entity.setPassword(bc.encode("admin"));
            userMapper.insert(entity);
        }
//        UUID uuid = UUID.randomUUID();
//        String uuidValue = uuid.toString().replaceAll("-", "");
//        uuidValue = uuidValue.substring(uuidValue.length() / 4 * 3);
//        User entity = new User();
//        entity.setNickname("用户" + System.currentTimeMillis());
//        entity.setUsername(uuidValue);
//        entity.setPassword(bc.encode(uuidValue));
//        userMapper.insert(entity);
        log.info(() -> "userTest");
    }

    @Test
    @Transactional
    public void transactionalAfter() {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 事务提交后执行
            }
        });

    }

    @Test
    public void retryTest() {
        RetryerBuilder<Void> retryBuilder = RetryerBuilder.newBuilder();
        Retryer<Void> retry = retryBuilder
            .withWaitStrategy(WaitStrategies.fixedWait(5, TimeUnit.SECONDS))
            .withStopStrategy(StopStrategies.stopAfterAttempt(3))
            .retryIfException()
            .build();

        try {
            retry.call(
                () -> {
                    // do something
                    return null;
                }
            );
        } catch (ExecutionException | RetryException ex) {
            log.error(ex::getMessage);
        }
    }
}
