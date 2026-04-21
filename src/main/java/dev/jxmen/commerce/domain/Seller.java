package dev.jxmen.commerce.domain;

public class Seller {

	private final String name;
	private final String email;
	private final String password;

	private Seller(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public static Seller signup(String name, String email, String rawPassword, PasswordEncoder passwordEncoder) {
		return new Seller(name, email, passwordEncoder.encode(rawPassword));
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
