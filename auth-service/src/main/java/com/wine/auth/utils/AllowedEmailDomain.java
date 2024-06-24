package com.wine.auth.utils;

import lombok.Getter;

@Getter
public enum AllowedEmailDomain {
    GMAIL("gmail.com"),
    OUTLOOK("outlook.com"),
    LIBERO("libero.it");

    private final String domain;

    AllowedEmailDomain(String domain) {
        this.domain = domain;
    }

}