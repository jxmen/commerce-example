package dev.jxmen.commerce.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {

	boolean existsByEmail(String email);
}
