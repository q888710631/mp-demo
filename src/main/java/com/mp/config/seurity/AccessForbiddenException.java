package com.mp.config.seurity;

import org.springframework.security.core.AuthenticationException;

public class AccessForbiddenException extends AuthenticationException {
    public AccessForbiddenException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AccessForbiddenException(String msg) {
        super(msg);
    }
}
