package dev.jxmen.commerce.presentation;

import dev.jxmen.commerce.application.SellerService;
import dev.jxmen.commerce.domain.DuplicateEmailException;
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
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SellerController.class)
@ExtendWith(RestDocumentationExtension.class)
class SellerControllerTest {

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
    void 셀러_회원가입_성공시_201을_반환한다() throws Exception {
        mockMvc.perform(post("/api/sellers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "테스트셀러",
                        "email": "seller@example.com",
                        "password": "password1234"
                    }
                    """))
            .andExpect(status().isCreated())
            .andDo(document("seller-signup",
                requestFields(
                    fieldWithPath("name").description("셀러 이름"),
                    fieldWithPath("email").description("셀러 이메일"),
                    fieldWithPath("password").description("셀러 비밀번호")
                )
            ));
    }

    @Test
    void 이미_존재하는_이메일로_회원가입시_409를_반환한다() throws Exception {
        doThrow(new DuplicateEmailException("seller@example.com"))
            .when(sellerService).signup(any(), any(), any());

        mockMvc.perform(post("/api/sellers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "테스트셀러",
                        "email": "seller@example.com",
                        "password": "password1234"
                    }
                    """))
            .andExpect(status().isConflict())
            .andDo(document("seller-signup-duplicate-email"));
    }

    @Test
    void 이메일_형식이_잘못된_경우_400을_반환한다() throws Exception {
        mockMvc.perform(post("/api/sellers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "테스트셀러",
                        "email": "invalid-email",
                        "password": "password1234"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andDo(document("seller-signup-invalid-email"));
    }

    static Stream<String> notBlankViolationBodies() {
        return Stream.of(
            // name 누락/공백
            """
            { "email": "seller@example.com", "password": "password1234" }
            """,
            """
            { "name": "", "email": "seller@example.com", "password": "password1234" }
            """,
            """
            { "name": "   ", "email": "seller@example.com", "password": "password1234" }
            """,
            // email 누락/공백
            """
            { "name": "테스트셀러", "password": "password1234" }
            """,
            """
            { "name": "테스트셀러", "email": "", "password": "password1234" }
            """,
            """
            { "name": "테스트셀러", "email": "   ", "password": "password1234" }
            """,
            // password 누락/공백
            """
            { "name": "테스트셀러", "email": "seller@example.com" }
            """,
            """
            { "name": "테스트셀러", "email": "seller@example.com", "password": "" }
            """,
            """
            { "name": "테스트셀러", "email": "seller@example.com", "password": "   " }
            """
        );
    }

    @ParameterizedTest
    @MethodSource("notBlankViolationBodies")
    void NotBlank_위반시_400을_반환한다(String body) throws Exception {
        mockMvc.perform(post("/api/sellers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }
}
