package org.jgy.chatserver.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtProvider {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private int expiration;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = new SecretKeySpec(Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
    }

    public String createToken(String email, String role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);

        Date now = new Date();

        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(now)
                   .setExpiration(new Date(now.getTime() + expiration * 60 * 1000L))
                   .signWith(key)
                   .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + claims.get("role", String.class))
        );

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return UsernamePasswordAuthenticationToken.authenticated(principal, token, authorities);
    }

    public String reIssueToken(String token) {
        Claims claims = parseClaims(token);
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        return createToken(email, role);
    }

    /**
     * @throws ExpiredJwtException
     */
    public void validateToken(String token) {
        try {
            parseClaims(token);
        } catch (MalformedJwtException | UnsupportedJwtException e) {
            throw new JwtException("토큰 형식이 올바르지 않습니다.", e);
        } catch (SignatureException e) {
            throw new JwtException("서명이 유효하지 않습니다.", e);
        } catch (IllegalArgumentException e) {
            throw new JwtException("token is empty", e);
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }
}
