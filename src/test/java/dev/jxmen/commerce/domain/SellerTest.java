package dev.jxmen.commerce.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SellerTest {

    private final PasswordEncoder passwordEncoder = rawPassword -> "{encoded}" + rawPassword;

    @Test
    void signup_시_셀러_관련_정보가_설정된다() {
        String name = "테스트셀러";
        String email = "seller@example.com";
        String password = "password1234";

        Seller seller = Seller.signup(name, email, password, passwordEncoder);

        assertThat(seller).isEqualTo(Seller.signup(name, email, password, passwordEncoder));
    }

    @Test
    void signup_시_비밀번호는_암호화된다() {
        Seller seller = Seller.signup("테스트셀러", "seller@example.com", "password1234", passwordEncoder);
        Seller sellerWithRawPassword = Seller.signup("테스트셀러", "seller@example.com", "password1234", raw -> raw);

        assertThat(seller).isNotEqualTo(sellerWithRawPassword);
    }
}
