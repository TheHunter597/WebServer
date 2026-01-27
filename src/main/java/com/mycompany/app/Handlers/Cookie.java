package com.mycompany.app.Handlers;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Cookie {

    private String name; // Required
    private String value; // Required

    private String domain;
    private String path = "/";

    private Long maxAge;
    private Instant expires;

    private boolean secure;
    private boolean httpOnly;
    private SameSite sameSite;

    protected Cookie() {
    }

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static String createCookieHeader(Cookie cookie) {
        StringBuilder sb = new StringBuilder();

        sb.append("Set-Cookie: ")
                .append(cookie.getName())
                .append("=")
                .append(cookie.getValue());
        if (cookie.getPath() != null) {
            sb.append("; Path=").append(cookie.getPath());
        }

        if (cookie.getDomain() != null) {
            sb.append("; Domain=").append(cookie.getDomain());
        }

        if (cookie.getMaxAge() != null) {
            sb.append("; Max-Age=").append(cookie.getMaxAge());
        }

        if (cookie.getExpires() != null) {
            sb.append("; Expires=").append(
                    DateTimeFormatter.RFC_1123_DATE_TIME
                            .withZone(ZoneOffset.UTC)
                            .format(cookie.getExpires()));
        }

        if (cookie.isSecure()) {
            sb.append("; Secure");
        }

        // HttpOnly
        if (cookie.isHttpOnly()) {
            sb.append("; HttpOnly");
        }

        // SameSite
        if (cookie.getSameSite() != null) {
            sb.append("; SameSite=").append(cookie.getSameSite().name());
        }

        return sb.toString();
    }

    public enum SameSite {
        STRICT,
        LAX,
        NONE
    }
}
