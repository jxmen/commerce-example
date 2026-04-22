package dev.jxmen.commerce.domain;

public class SellerNotFoundException extends RuntimeException {

	public SellerNotFoundException(String email) {
		super("존재하지 않는 이메일입니다: " + email);
	}
}
