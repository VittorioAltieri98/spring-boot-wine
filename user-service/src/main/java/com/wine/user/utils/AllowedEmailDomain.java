package com.wine.user.utils;

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