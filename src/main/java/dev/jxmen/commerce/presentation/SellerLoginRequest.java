package dev.jxmen.commerce.presentation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SellerLoginRequest(
	@NotBlank @Email String email,
	@NotBlank String rawPassword
) {
}
