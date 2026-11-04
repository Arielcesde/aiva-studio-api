package com.aivastudio.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aivastudio.model.entity.AuthToken;
import com.aivastudio.model.entity.User;
import com.aivastudio.repository.AuthTokenRepository;

@Service
public class AuthTokenService {

    private static final Duration TOKEN_LIFETIME = Duration.ofHours(12);
    private final SecureRandom secureRandom = new SecureRandom();
    private final AuthTokenRepository authTokenRepository;

    public AuthTokenService(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    @Transactional
    public String issue(User user) {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        authTokenRepository.save(new AuthToken(
                user,
                hash(token),
                Instant.now().plus(TOKEN_LIFETIME)));
        return token;
    }

    @Transactional(readOnly = true)
    public Optional<User> authenticate(String token) {
        return authTokenRepository
                .findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(hash(token), Instant.now())
                .map(authToken -> authToken.getUser());
    }

    @Transactional
    public void revoke(String token) {
        authTokenRepository.revokeByTokenHash(hash(token), Instant.now());
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (java.security.NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 no está disponible", ex);
        }
    }
}
