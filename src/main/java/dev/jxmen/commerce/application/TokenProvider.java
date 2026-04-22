package dev.jxmen.commerce.application;

public interface TokenProvider {
    SellerTokens generate(String email);
}
