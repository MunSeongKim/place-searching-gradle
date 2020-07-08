package com.mskim.search.place.configuration.security.support;

import lombok.Getter;

@Getter
public enum SecurityConstant {
    SECURITY_LOGIN_URL_PATH("/view/auth/sign_in"),
    SECURITY_LOGIN_PROCESS_URL_PATH("/auth/validation"),
    SECURITY_LOGOUT_URL_PATH("/auth/sign_out"),
    SECURITY_SUCCESS_REDIRECT_URL("/"),
    SECURITY_USERNAME_KEY("id"),
    SECURITY_PASSWORD_KEY("password"),
    SECURITY_ORIGINAL_REDIRECT_URL_KEY("ORIGIN_REDIRECT_URL");

    private String value;

    SecurityConstant(String constant) {
        this.value = constant;
    }
}
