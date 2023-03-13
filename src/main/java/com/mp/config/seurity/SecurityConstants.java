package com.mp.config.seurity;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

public class SecurityConstants {
    // 角色前缀
    public static final String ROLE_PREFIX = "ROLE_";
    // 任意角色
    public static final String ROLE_ANY = ROLE_PREFIX + "any";
    // 管理员
    public static final String ROLE_ADMIN = ROLE_PREFIX + "admin";
    // 运营
    public static final String ROLE_BIZ = ROLE_PREFIX + "biz";
    // 客服
    public static final String ROLE_CUSTOM_SERVICE = ROLE_PREFIX + "custom_service";
    // 商户
    public static final String ROLE_MCH = ROLE_PREFIX + "mch";
    // 游客
    public static final String ROLE_USER = ROLE_PREFIX + "user";
    // token无效时的角色
    public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

    /**
     * 格式化role
     *
     * @param roleName admin
     * @return ROLE_admin
     */
    public static String roleFormat(String roleName) {
        if (StringUtils.isNotBlank(roleName)) {
            if (roleName.startsWith(ROLE_PREFIX)) {
                return ROLE_PREFIX + roleName.replace(ROLE_PREFIX, "").toLowerCase();
            }
            return ROLE_PREFIX + roleName.toLowerCase();
        }
        return roleName;
    }

    public static Collection<GrantedAuthority> createAuthorities(String role) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(SecurityConstants.roleFormat(role));
        authorities.add(authority);
        return authorities;
    }

}
