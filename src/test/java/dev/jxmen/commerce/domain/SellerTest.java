package dev.jxmen.commerce.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SellerTest {

    private final PasswordEncoder passwordEncoder = new PasswordEncoder() {
        @Override
        public String encode(String rawPassword) { return "{encoded}" + rawPassword; }
        @Override
        public boolean matches(String rawPassword, String encodedPassword) { return encodedPassword.equals("{encoded}" + rawPassword); }
    };

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
        PasswordEncoder identity = new PasswordEncoder() {
            @Override
            public String encode(String rawPassword) { return rawPassword; }
            @Override
            public boolean matches(String rawPassword, String encodedPassword) { return rawPassword.equals(encodedPassword); }
        };
        Seller sellerWithRawPassword = Seller.signup("테스트셀러", "seller@example.com", "password1234", identity);

        assertThat(seller).isNotEqualTo(sellerWithRawPassword);
    }

    @Test
    void matchesPassword_시_올바른_비밀번호면_true를_반환한다() {
        Seller seller = Seller.signup("테스트셀러", "seller@example.com", "password1234", passwordEncoder);

        assertThat(seller.matchesPassword("password1234", passwordEncoder)).isTrue();
    }

    @Test
    void matchesPassword_시_틀린_비밀번호면_false를_반환한다() {
        Seller seller = Seller.signup("테스트셀러", "seller@example.com", "password1234", passwordEncoder);

        assertThat(seller.matchesPassword("wrongpassword", passwordEncoder)).isFalse();
    }
}
