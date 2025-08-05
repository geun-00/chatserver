package org.jgy.chatserver.common.auth;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(token)) {
            String accessToken = extractAccessToken(token);

            try {
                jwtProvider.validateToken(accessToken);
                setAuthentication(accessToken);
            } catch (ExpiredJwtException e) {
                accessToken = jwtProvider.reIssueToken(accessToken);
                response.setHeader(AUTHORIZATION_HEADER, accessToken);
                setAuthentication(accessToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        SecurityContextHolder.getContextHolderStrategy().getContext().setAuthentication(
                jwtProvider.getAuthentication(accessToken)
        );
    }

    private String extractAccessToken(String token) {
        if (!token.startsWith(TOKEN_PREFIX)) {
            throw new AuthenticationServiceException("토큰 형식이 잘못 되었습니다.");
        }

        return token.substring(TOKEN_PREFIX.length());
    }
}
