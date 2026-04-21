package dev.jxmen.commerce.application;

import dev.jxmen.commerce.domain.DuplicateEmailException;
import dev.jxmen.commerce.domain.PasswordEncoder;
import dev.jxmen.commerce.domain.Seller;
import dev.jxmen.commerce.domain.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SellerService {

	private final SellerRepository sellerRepository;
	private final PasswordEncoder passwordEncoder;

	public SellerService(SellerRepository sellerRepository, PasswordEncoder passwordEncoder) {
		this.sellerRepository = sellerRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public void signup(String name, String email, String password) {
		if (sellerRepository.existsByEmail(email)) {
			throw new DuplicateEmailException(email);
		}
		sellerRepository.save(Seller.signup(name, email, password, passwordEncoder));
	}
}
