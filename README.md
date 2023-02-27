# mp-demo
spring-boot + mybatis plus 多租户

## 2023.2.27 

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