package dev.jxmen.commerce.infrastructure;

import dev.jxmen.commerce.application.SellerTokens;
import dev.jxmen.commerce.application.TokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider implements TokenProvider {

	private final SecretKey secretKey;
	private final long accessTokenExpiry;
	private final long refreshTokenExpiry;

	public JwtTokenProvider(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.access-token-expiry}") long accessTokenExpiry,
		@Value("${jwt.refresh-token-expiry}") long refreshTokenExpiry
	) {
		this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
		this.accessTokenExpiry = accessTokenExpiry;
		this.refreshTokenExpiry = refreshTokenExpiry;
	}

	@Override
	public SellerTokens generate(String email) {
		return new SellerTokens(
			generateToken(email, accessTokenExpiry),
			generateToken(email, refreshTokenExpiry)
		);
	}

	private String generateToken(String email, long expiry) {
		Date now = new Date();
		return Jwts.builder()
			.subject(email)
			.issuedAt(now)
			.expiration(new Date(now.getTime() + expiry))
			.signWith(secretKey)
			.compact();
	}
}
