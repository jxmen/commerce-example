package dev.jxmen.commerce.presentation;

import dev.jxmen.commerce.application.SellerService;
import dev.jxmen.commerce.application.SellerTokens;
import dev.jxmen.commerce.domain.InvalidPasswordException;
import dev.jxmen.commerce.domain.SellerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SellerController.class)
@ExtendWith(RestDocumentationExtension.class)
class SellerLoginControllerTest {

    private MockMvc mockMvc;

    @MockitoBean
    private SellerService sellerService;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @Test
    void 셀러_로그인_성공시_200과_토큰을_반환한다() throws Exception {
        when(sellerService.login("seller@example.com", "password1234"))
            .thenReturn(new SellerTokens("access-token", "refresh-token"));

        mockMvc.perform(post("/api/sellers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "seller@example.com",
                        "rawPassword": "password1234"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("access-token"))
            .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
            .andDo(document("seller-login",
                requestFields(
                    fieldWithPath("email").description("셀러 이메일"),
                    fieldWithPath("rawPassword").description("셀러 비밀번호")
                ),
                responseFields(
                    fieldWithPath("accessToken").description("액세스 토큰"),
                    fieldWithPath("refreshToken").description("리프레시 토큰")
                )
            ));
    }

    @Test
    void 존재하지_않는_이메일로_로그인시_401과_SELLER_NOT_FOUND_에러코드를_반환한다() throws Exception {
        when(sellerService.login(any(), any()))
            .thenThrow(new SellerNotFoundException("notexist@example.com"));

        mockMvc.perform(post("/api/sellers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "notexist@example.com",
                        "rawPassword": "password1234"
                    }
                    """))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("SELLER_NOT_FOUND"))
            .andDo(document("seller-login-seller-not-found",
                responseFields(
                    fieldWithPath("code").description("에러 코드 (SELLER_NOT_FOUND)")
                )
            ));
    }

    @Test
    void 비밀번호가_일치하지_않을_경우_401과_INVALID_PASSWORD_에러코드를_반환한다() throws Exception {
        when(sellerService.login(any(), any()))
            .thenThrow(new InvalidPasswordException());

        mockMvc.perform(post("/api/sellers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "seller@example.com",
                        "rawPassword": "wrongpassword"
                    }
                    """))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_PASSWORD"))
            .andDo(document("seller-login-invalid-password",
                responseFields(
                    fieldWithPath("code").description("에러 코드 (INVALID_PASSWORD)")
                )
            ));
    }

    static Stream<String> loginNotBlankViolationBodies() {
        return Stream.of(
            // email 누락/공백
            """
            { "rawPassword": "password1234" }
            """,
            """
            { "email": "", "rawPassword": "password1234" }
            """,
            """
            { "email": "   ", "rawPassword": "password1234" }
            """,
            // rawPassword 누락/공백
            """
            { "email": "seller@example.com" }
            """,
            """
            { "email": "seller@example.com", "rawPassword": "" }
            """,
            """
            { "email": "seller@example.com", "rawPassword": "   " }
            """
        );
    }

    @ParameterizedTest
    @MethodSource("loginNotBlankViolationBodies")
    void 필수값이_누락되거나_공백인_경우_400을_반환한다(String body) throws Exception {
        mockMvc.perform(post("/api/sellers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }
}
