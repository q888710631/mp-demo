package com.mp.config.jwt.applet;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author wu.dunhong
 */
public class AppletAuthenticationToken extends AbstractAuthenticationToken {

    private Long accountId;

    public AppletAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Long accountId) {
        super(authorities);
        this.accountId = accountId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return "";
    }
}
