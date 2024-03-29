# mp-demo
spring boot + mybatis plus 多租户 以及各种框架整合。

主要框架：

1. spring boot 2.7.8 + undertow
2. mybatis plus + pagehelper
3. spring security + jwt
4. openfeign
5. kafka
6. nacos
7. sleuth
8. springdoc-openapi
9. redisson
10. websocket

初始化sql： `resource/sql/init.sql`

目录：
```text
mp-demo
├── app 主体
└── cover 存放覆盖源码的类
```

## 2023.5.10
Mybatis的`AbstractJsonTypeHandler`的实现类

1. `MybatisJsonTypeEntityHandler`用于处理`对象`字段，可以直接使用
2. `MybatisJsonTypeListHandler`用于处理`数组`字段，使用时`需要继承`后使用

```java
/**
 * 指定处理List<Role>
 */
public class ListRoleHandler extends MybatisJsonTypeListHandler<Role>{
    
}

```

```java
/**
 * 设置autoResultMap = true
 */
@TableName(value = "person", autoResultMap = true)
public class Person {
   /**
    * 字段类型为 varchar
    */
   @TableField(value = "role_list", typeHandler = ListRoleHandler.class)
   private List<Role> roleList;

   /**
    * 字段类型为 varchar
    */
   @TableField(value = "role", typeHandler = MybatisJsonTypeEntityHandler.class)
   private Role role;
}


```


## 2023.5.9
调整日期类序列化格式

1. Request入参，支持ISO 8601（2023-05-09T12:15:30.00Z），yyyy-MM-dd HH:mm:ss，1683613823128（13位），1683613823（10位）
2. ObjectMapper转json，格式yyyy-MM-dd HH:mm:ss
3. @Cacheable序列化和反序列化，格式yyyy-MM-dd HH:mm:ss


## 2023.5.8
新增限流注解`@RateLimit`

限流模式： 
1. 强力模式：成功锁定后不解锁，等待时间自动解锁 
2. 通用限流：使用漏桶模式
3. ip限流：使用漏桶模式

```java
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @RateLimit
    @PostMapping
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(new HashMap<>());
    }

}
```

## 2023.5.5
通过Nacos增强飞书日志通知功能

Nacos中增加如下配置，可用于过滤（String.contains）不需要的日志通知

例如有日志：
1. 校验错误：身份证格式不正确
2. 校验错误：手机号格式不正确
3. ...

在nacos配置文件（对应配置类NacosCustomProperties）中：
```yaml
feishu-log-filter:
  - 校验错误
```


## 2023.4.24
新增json工具类`JsonUtil.merge`用于合并json对象

参考`JsonTests`


## 2023.4.23
新增日志异步输出到飞书功能`FeiShuAlertAppender`

```yaml
application:
  feishu:
    # 飞书机器人使能
    enable:
      log-notify: true
    # 飞书机器人hook
    group:
      log-notify: 飞书机器人的hook
```

通知示例：
```text
异常告警-ERROR
告警环境： dev,exclude,kafka-single,websocket
应用名称： honyee
出现时间： 2023-04-23 13:42:13
traceId： dca8a04bfdc376e4
spanId ： dca8a04bfdc376e4
Request URI： /api/test/evict
Request Method： GET
Request Param： {"ids":[1, 2, 3]}
Request Body：
错误信息： IndexOutOfBoundsException：测试错误
    at com.honyee.app.web.TestController.evictTest(83)
```

## 2023.4.21
新增线程装饰器`TaskContextDecorator`，用于线程中获取上下文
```java
public class TestService{
    public void test(){
       Runnable runnable = new TaskContextDecorator().decorate(() -> {
          RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
          if (requestAttributes != null) {
             ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
             HttpServletRequest request = attributes.getRequest();
             TraceContext traceContext = (TraceContext) request.getAttribute(TraceContext.class.getName());
             String traceId = traceContext.traceId();
          }
       });
       new Thread(runnable).start();
    }
}
```

## 2023.4.14
`@CacheEvict`支持模糊删除指定前缀的缓存（重写了`CacheAspectSupport`和`RedisCache`）

```java
public class CacheService {
   // 清理前缀为prefix的所有缓存（根据key，支持SPEL）
   @CacheEvict(value = "cache-test", key = "#prefix", allEntries = true)
   public void evictTest(String prefix) {
   }    
}

```



## 2023.4.1
整合WebSocket STOMP，需在maven启用websocket选项，以及在`authentication.yml`配置权限

执行顺序：
1. `WebsocketHandshakeInterceptor` 握手拦截
2. `WebsocketHandshakeHandler` 鉴权拦截
3. `WebsocketAuthChannelInterceptor` 消息到达前拦截
4. `WebsocketController` Controller

示例： `WebsocketController` 和 `/resource/html/websocket.html`


## 2023.3.31
整合Naocs

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        username: nacos
        password: nacos
        namespace:
        enabled: true
```

```java
/**
 * Nacos配置
 */
public class NacosConfiguration implements InitializingBean {
   @Override
   public void afterPropertiesSet() throws Exception {
       // 配置变动监听
      nacosConfigManager.getConfigService().addListener(getDataId(), nacosConfigProperties.getGroup(), new NacosListener());
      
      // 订阅
      NotifyCenter.registerSubscriber(new NacosInstancesChangeNotifier());
   }
   
   @EventListener
   public void onApplicationEvent(HeartbeatEvent event) {
      // 实例心跳监听
   }
}

```

```java
/**
 * 配置变动监听
 */
public class NacosListener implements Listener {

     @Override
     public Executor getExecutor() {
         return null;
     }

     @Override
     public void receiveConfigInfo(String configInfo) {
         boolean result = analysisConfig(configInfo);
     }
 }
```

```java
/**
 * 实例变动事件
 */
public class NacosInstancesChangeNotifier extends Subscriber<InstancesChangeEvent> {
   @Override
   public void onEvent(InstancesChangeEvent event) {
      // do something
   }

   @Override
   public Class<? extends Event> subscribeType() {
      return InstancesChangeEvent.class;
   }
}
```

## 2023.3.30
1. 接入飞书机器人消息

```yaml
application:
  feishu:
    group:
      common: 飞书机器人的hook
```

```java
@Service
public class TestService {

    @Resource
    FeishuService feishuService;

    public String feishu() {
      FeishuMessageRequeset feishuMessageRequeset = new FeishuMessageRequeset();
      feishuMessageRequeset.setTitle("测试消息");
      feishuMessageRequeset.addMsg("请说", "hello honyee");
      feishuService.send(feishuMessageRequeset);
    }

}
```

2. 新增延时任务实现`DelayTaskListener<T>`，可以指定由线程或Kafka执行任务
```yaml
application:
  delay-task:
    # 通过kafka实际调用任务，前提是先启用kafka
    kafka-execute: true
```

```java
/**
 * 1. 必须实现Serializable
 * 2. 交由kafka执行时注解 @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class MyDelayParam implements Serializable {
    private long id;
    private String title;
    // 省略get&set
}
```

实现监听
```java
@Component
public class MyDelayTaskListener implements DelayTaskListener<MyDelayParam> {

    @Override
    public void run(MyDelayParam myDelayParam) {
        System.out.println("MyDelayTaskListener");
    }
}
```

提交延时任务
```java
@Service
public class TestService {

    public void delay() {
        MyDelayParam myDelayParam = new MyDelayParam();
        myDelayParam.setId(System.currentTimeMillis());
        myDelayParam.setTitle("延时任务");
        // 延时
        DelayTaskConfiguration.submit(myDelayParam, 5, TimeUnit.SECONDS);
        // 指定时间
        DelayTaskConfiguration.submit(myDelayParam, Instant.now().plusSeconds(5));
    }

}
```

## 2023.3.28
1. 整合kafka，默认单个实例，对应配置文件：`application-kafka-singlel.yml`

    maven开启kafka-mul可配置多个实例，对应配置文件：`application-kafka-mul.yml`、`KafkaMulConfiguration`

    maven开启exclude支持排除Kafka，对应配置文件：`application-exclude.yml`

2. 新增`@RedisLock`注解，在Method上加锁，使用方式类似`@Cacheable`，支持`SpEl`

```java
@Service
public class TestService {

    @RedisLock(value = "test", key = "#query")
    public String lockTest(String query) {
        return "complete";
    }
}
```

## 2023.3.27

优化mybatis sql日志打印，并拆分模块`app`、`cover`

## 2023.3.24

1. 日志打印工具封装`LogUtil`

2. 优化feign日志打印，使request和response日志分别一次性输出。

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

## 2023.3.15
接入springdoc

apifox导入地址：http://localhost:1222/v3/api-docs
```yaml
# 禁用文档
springdoc:
  api-docs:
    enabled: false
```


## 2023.2.27 
1. 获取租户ID `TenantHelper.getTenantId()`

2. 补充`@InterceptorIgnore`的支持范围

```java
/**
 * 支持Entity
 */
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

```java
/**
 * 支持Class
 */
@SpringBootTest
@InterceptorIgnore(tenantLine = "true")
class MpApplicationTests {
    
}
```

```java
/**
 * 支持Method
 */
@SpringBootTest
class MpApplicationTests {
    @Test
    @InterceptorIgnore(tenantLine = "true")
    public void emptyInit() {
        log.info(() -> "emptyInit");
    }
}
```