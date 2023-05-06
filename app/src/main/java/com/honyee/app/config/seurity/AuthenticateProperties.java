package com.honyee.app.config.seurity;

import java.util.List;

public class AuthenticateProperties {
    // url权限配置
    private List<AuthenticateMatcher> authenticateMatchers;

    // menu权限配置
    private List<AuthenticateMatcher> menuMatchers;

    public List<AuthenticateMatcher> getAuthenticateMatchers() {
        return authenticateMatchers;
    }

    public void setAuthenticateMatchers(List<AuthenticateMatcher> authenticateMatchers) {
        this.authenticateMatchers = authenticateMatchers;
    }

    public List<AuthenticateMatcher> getMenuMatchers() {
        return menuMatchers;
    }

    public void setMenuMatchers(List<AuthenticateMatcher> menuMatchers) {
        this.menuMatchers = menuMatchers;
    }
}
