package org.jgy.chatserver.common.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("JwtProviderTest")
class JwtProviderTest {

    private final JwtProvider jwtProvider = new JwtProvider();

    private final String email = "johndoe@gmail.com";
    private final String role = "USER";

    @BeforeEach
    void setUp() {
        int expiration = 60;
        String secretKey = Base64.getEncoder().encodeToString("testSecretkey1234567890testSecretkey1234567890".getBytes());

        ReflectionTestUtils.setField(jwtProvider, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtProvider, "expiration", expiration);
        jwtProvider.init();
    }

    @Test
    @DisplayName("토큰을 생성하면 헤더, 페이로드, 시그니처 부분으로 구분된다.")
    void createToken() {
        //given
        //when
        String token = getToken();

        //then
        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("생성된 토큰으로 인증 객체를 만들 수 있다.")
    void getAuthentication() {
        //given
        String token = getToken();

        //when
        Authentication authentication = jwtProvider.getAuthentication(token);

        //then
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo(email);
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getAuthorities()).hasSize(1);
        assertThat(authentication.getAuthorities()).extracting("authority").contains("ROLE_" + role);
    }

    @Test
    @DisplayName("과거 토큰으로 새로운 토큰을 생성할 수 있다.")
    void reIssueToken() {
        //given
        String prevToken = getToken();

        //when
        String newToken = jwtProvider.reIssueToken(prevToken);

        //then
        assertThat(newToken).isNotNull();
        assertThat(newToken).isNotEqualTo(prevToken);
        assertThat(newToken.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("토큰 검증에 성공하면 예외가 발생하지 않는다.")
    void validateToken_success() {
        //given
        String token = getToken();

        //when
        //then
        assertDoesNotThrow(() -> jwtProvider.validateToken(token));
    }

    @Test
    @DisplayName("유효 기간이 지나면 ExpiredJwtException 예외가 발생한다.")
    void validateToken_fail() {
        //given
        ReflectionTestUtils.setField(jwtProvider, "expiration", -1);
        String token = getToken();

        //when
        //then
        assertThatThrownBy(() -> jwtProvider.validateToken(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("토큰 형식이 잘못되면 MalformedJwtException 예외가 발생한다.")
    void test() {
        //given
        String token = getToken();
        String invalidToken = token.substring(1);

        //when
        //then
        assertThatThrownBy(() -> jwtProvider.validateToken(invalidToken))
                .isInstanceOf(JwtException.class)
                .hasCauseInstanceOf(MalformedJwtException.class)
                .hasMessage("토큰 형식이 올바르지 않습니다.");
    }

    private String getToken() {
        return jwtProvider.createToken(email, role);
    }
}