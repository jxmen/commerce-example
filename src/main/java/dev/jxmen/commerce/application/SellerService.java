package dev.jxmen.commerce.application;

import dev.jxmen.commerce.domain.DuplicateEmailException;
import dev.jxmen.commerce.domain.InvalidPasswordException;
import dev.jxmen.commerce.domain.PasswordEncoder;
import dev.jxmen.commerce.domain.Seller;
import dev.jxmen.commerce.domain.SellerNotFoundException;
import dev.jxmen.commerce.domain.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SellerService {

	private final SellerRepository sellerRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;

	public SellerService(SellerRepository sellerRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
		this.sellerRepository = sellerRepository;
		this.passwordEncoder = passwordEncoder;
		this.tokenProvider = tokenProvider;
	}

	public void signup(String name, String email, String password) {
		if (sellerRepository.existsByEmail(email)) {
			throw new DuplicateEmailException(email);
		}
		sellerRepository.save(Seller.signup(name, email, password, passwordEncoder));
	}

	public SellerTokens login(String email, String rawPassword) {
		Seller seller = sellerRepository.findByEmail(email)
				.orElseThrow(() -> new SellerNotFoundException(email));

		if (!seller.matchesPassword(rawPassword, passwordEncoder)) {
			throw new InvalidPasswordException();
		}

		return tokenProvider.generate(email);
	}
}
