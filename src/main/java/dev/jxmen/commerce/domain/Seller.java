package dev.jxmen.commerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sellers")
public class Seller {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	protected Seller() {}

	private Seller(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public static Seller signup(String name, String email, String rawPassword, PasswordEncoder passwordEncoder) {
		return new Seller(name, email, passwordEncoder.encode(rawPassword));
	}

	public boolean matchesPassword(String rawPassword, PasswordEncoder passwordEncoder) {
		return passwordEncoder.matches(rawPassword, this.password);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Seller other)) return false;
		return java.util.Objects.equals(email, other.email)
				&& java.util.Objects.equals(password, other.password)
				&& java.util.Objects.equals(name, other.name);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(email, password, name);
	}
}
