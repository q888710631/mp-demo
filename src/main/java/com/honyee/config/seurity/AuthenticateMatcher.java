package com.honyee.config.seurity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

public class AuthenticateMatcher {

    /**
     * 映射路径
     */
    @JsonProperty("path-matchers")
    List<String> pathMatchers;

    /**
     * 匹配方法
     */
    @JsonProperty("http-method")
    private HttpMethod httpMethod;

    /**
     * 是否需要授权
     */
    @JsonProperty("authenticated")
    private Boolean authenticated;

    /**
     * 指定角色
     */
    @JsonProperty("has-any-role")
    private List<String> hasAnyRole;

    /**
     * 指定权限
     */
    @JsonProperty("has-any-authority")
    private List<String> hasAnyAuthority;

    public AuthenticateMatcher() {
        this.authenticated = Boolean.TRUE;
        this.pathMatchers = new ArrayList<>();
        this.hasAnyRole = new ArrayList<>();
        this.hasAnyAuthority = new ArrayList<>();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public List<String> getPathMatchers() {
        return pathMatchers;
    }

    public void setPathMatchers(List<String> pathMatchers) {
        this.pathMatchers = pathMatchers;
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public List<String> getHasAnyRole() {
        return hasAnyRole;
    }

    public void setHasAnyRole(List<String> hasAnyRole) {
        this.hasAnyRole = hasAnyRole;
    }

    public List<String> getHasAnyAuthority() {
        return hasAnyAuthority;
    }

    public void setHasAnyAuthority(List<String> hasAnyAuthority) {
        this.hasAnyAuthority = hasAnyAuthority;
    }

    @Override
    public String toString() {
        return "AuthenticateMatcher{" +
            "pathMatchers=" + pathMatchers +
            ", httpMethod=" + httpMethod +
            ", authenticated=" + authenticated +
            ", hasAnyRole=" + hasAnyRole +
            ", hasAnyAuthority=" + hasAnyAuthority +
            '}';
    }
}
