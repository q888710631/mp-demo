# mp-demo
spring boot + mybatis plus 多租户

主要框架：

1. spring boot 2.7.8 + undertow
2. mybatis plus + pagehelper
3. spring security + jwt
4. openfeign
5. sleuth
6. springdoc-openapi
7. redisson
8. mapstruct

初始化sql： `resource/sql/init.sql`

目录：
```text
mp-demo
├── app 主体
└── cover 存放覆盖源码的类
```

## 2023.3.27
优化mybatis sql日志打印，并拆分模块`app`、`cover`

## 2023.3.24
优化feign日志打印，使request和response日志分别一次性输出。

可通过配置application.yml或者注解`@DisableFeignLog`来开启/关闭日志

```yaml
# feign日志级别
feign:
  client:
    config:
      default:
        loggerLevel: FULL
```

## 2023.3.23
整合`springdoc-openapi-ui`，通过`http://localhost:1222/v3/api-docs` 导入接口到apifox

## 2023.3.22

新增一些工具类

```java
public class MyTest {
    public void test() {
        String url = "https://www.honyee.com/html/honyee.html?id=123&id=456&type=abc&data.name=honyee";
        ParamDTO paramDTO = HttpUtil.readQuery(url, ParamDTO.class);
    }
}
```

```java
public class ParamDTO {
    List<String> id;
    String type;
    Data data;
    public static class Data{
        String name;
        // 省略 get&set
    }
    
    // 省略 get&set
}
```

## 2023.3.19
`@Cacheable`新增缓存配置`redisCacheManager`和`cacheManagerTwice`

`redisCacheManager`缓存到redis，可自定义每个value的缓存时长

`cacheManagerTwice`缓存到内存，固定时长

```java
@Service
public class CacheService {
    /**
     * value后面#60表示缓存60秒，不配置cacheManager时默认使用redisCacheManager
     * 缓存时长：
     * 1. 如果没有"#"则默认不设置过期时间
     * 2. 拼接的第一个"#"后面为过期时间，第二个"#"后面为时间单位
     * 3. 时间单位的表示使用: d(天)、h(小时)、m(分钟)、s(秒), 默认为s(秒)
     */
    @Cacheable(value = "cache-redis#60", key = "#key")
    public void cacheRedis(String key) {
        System.out.println("cache-redis");
    }

    @Cacheable(value = "cache-memory", key = "#key", cacheManager = "memoryCacheManager")
    public void cacheMemory(String key) {
        System.out.println("cache-memory");
    }
}
```




## 2023.3.16
新增`url权限配置`和`菜单权限配置`，存储于`authentication.yml`

```yaml
authenticate-matchers:
  # 测试
  - path-matchers:
      - /api/test/**
    authenticated: false
  # api-docs
  - path-matchers:
      - /v3/api-docs
    authenticated: false
  # 登录授权
  - path-matchers:
      - /api/authenticate
    authenticated: false
  # 通用接口
  - path-matchers:
      - /api/user-menu
      - /api/all-role
  # 管理员接口
  - path-matchers:
      - /api/admin/**
    has-any-role:
      - admin

# 菜单权限
menu-matchers:
  - path-matchers:
      - /index
  - path-matchers:
      - /user/index
    authenticated: true
    has-any-role:
      - admin


```



## 2023.2.27 

### @InterceptorIgnore补充

1. 支持Entity
```java
@TableName("tenant")
@InterceptorIgnore(tenantLine = "true")
public class Tenant extends BaseEntity{
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("nick_name")
    private String nickName;
    // get & set ...
}
```

2. 支持Class
```java
@SpringBootTest
@InterceptorIgnore(tenantLine = "true")
class MpApplicationTests {
    
}
```

3. 支持Method
```java
@SpringBootTest
class MpApplicationTests {
    @Test
    @InterceptorIgnore(tenantLine = "true")
    public void emptyInit() {
        log.info(() -> "emptyInit");
    }
}
```