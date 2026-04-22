package dev.jxmen.commerce.application;

import dev.jxmen.commerce.domain.InvalidPasswordException;
import dev.jxmen.commerce.domain.PasswordEncoder;
import dev.jxmen.commerce.domain.Seller;
import dev.jxmen.commerce.domain.SellerNotFoundException;
import dev.jxmen.commerce.domain.SellerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private SellerService sellerService;

    private static PasswordEncoder identityEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(String rawPassword) { return rawPassword; }
            @Override
            public boolean matches(String rawPassword, String encodedPassword) { return rawPassword.equals(encodedPassword); }
        };
    }

    @Test
    void 이메일이_존재하지_않으면_SellerNotFoundException을_던진다() {
        when(sellerRepository.findByEmail("notexist@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sellerService.login("notexist@example.com", "password1234"))
            .isInstanceOf(SellerNotFoundException.class);
    }

    @Test
    void 비밀번호가_일치하지_않으면_InvalidPasswordException을_던진다() {
        Seller seller = Seller.signup("테스트셀러", "seller@example.com", "password1234", identityEncoder());
        when(sellerRepository.findByEmail("seller@example.com")).thenReturn(Optional.of(seller));
        when(passwordEncoder.matches("wrongpassword", "password1234")).thenReturn(false);

        assertThatThrownBy(() -> sellerService.login("seller@example.com", "wrongpassword"))
            .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    void 로그인_성공시_accessToken과_refreshToken을_반환한다() {
        Seller seller = Seller.signup("테스트셀러", "seller@example.com", "password1234", identityEncoder());
        when(sellerRepository.findByEmail("seller@example.com")).thenReturn(Optional.of(seller));
        when(passwordEncoder.matches("password1234", "password1234")).thenReturn(true);
        when(tokenProvider.generate("seller@example.com")).thenReturn(new SellerTokens("access-token", "refresh-token"));

        SellerTokens tokens = sellerService.login("seller@example.com", "password1234");

        assertThat(tokens.accessToken()).isNotNull();
        assertThat(tokens.refreshToken()).isNotNull();
    }
}
