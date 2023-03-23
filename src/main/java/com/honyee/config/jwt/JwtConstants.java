package com.honyee.config.jwt;

public class JwtConstants {
    public static final long TOKEN_VALID_MS = 1000 * 1800L;

    public static final long TOKEN_VALID_MS_FOR_REMEMBER_ME = 1000 * 2592000L;

    public static final String AUTHORITIES_KEY = "auth";

    public static final String LOGIN_TYPE = "LOGIN_TYPE";

    public static final String LOGIN_KEY = "LOGIN_KEY";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String AUTHORIZATION_PARAM = "access_token";

    public static final String BEARER = "Bearer";
}
