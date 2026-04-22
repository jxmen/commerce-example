package dev.jxmen.commerce.infrastructure;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BcryptPasswordEncoderTest {

    private final BcryptPasswordEncoder encoder = new BcryptPasswordEncoder();

    @Test
    void encode_시_원본_비밀번호와_다른_값을_반환한다() {
        String rawPassword = "password1234";

        String encoded = encoder.encode(rawPassword);

        assertThat(encoded).isNotEqualTo(rawPassword);
    }

    @Test
    void encode_시_같은_입력이라도_매번_다른_값을_반환한다() {
        String rawPassword = "password1234";

        String first = encoder.encode(rawPassword);
        String second = encoder.encode(rawPassword);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void matches_시_올바른_비밀번호면_true를_반환한다() {
        String rawPassword = "password1234";
        String encoded = encoder.encode(rawPassword);

        assertThat(encoder.matches(rawPassword, encoded)).isTrue();
    }

    @Test
    void matches_시_틀린_비밀번호면_false를_반환한다() {
        String encoded = encoder.encode("password1234");

        assertThat(encoder.matches("wrongpassword", encoded)).isFalse();
    }
}
