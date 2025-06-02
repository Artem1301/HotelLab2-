package com.artem.HotelBookingApplication.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "your_jwt_secret_key");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000L);
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtils.generateToken("test@example.com");
        assertNotNull(token);
        assertTrue(token.length() > 0);

        String username = jwtUtils.getUserNameFromToken(token);
        assertEquals("test@example.com", username);
    }

    @Test
    void testValidateToken_Valid() {
        String token = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS512, "your_jwt_secret_key")
                .compact();

        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void testValidateToken_Invalid() {
        String invalidToken = "invalid.token.here";
        assertFalse(jwtUtils.validateToken(invalidToken));
    }
}