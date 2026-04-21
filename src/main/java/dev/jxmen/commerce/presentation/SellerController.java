package dev.jxmen.commerce.presentation;

import dev.jxmen.commerce.application.SellerService;
import dev.jxmen.commerce.domain.DuplicateEmailException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

	private final SellerService sellerService;

	public SellerController(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void signup(@RequestBody @Valid SellerSignupRequest request) {
		sellerService.signup(request.name(), request.email(), request.password());
	}

	@ExceptionHandler(DuplicateEmailException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public void handleDuplicateEmail() {}
}
