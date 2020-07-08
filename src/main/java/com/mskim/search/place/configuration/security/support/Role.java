package com.mskim.search.place.configuration.security.support;

import lombok.Getter;

@Getter
public enum Role {
    USER("ROLE_USER");

    private String value;

    Role(String role) {
        this.value = role;
    }
}
