package dev.jxmen.commerce.domain;

public interface PasswordEncoder {
    String encode(String rawPassword);
}
