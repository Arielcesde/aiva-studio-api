package com.aivastudio.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aivastudio.exception.ReglaDeNegocioException;
import com.aivastudio.model.dto.PasswordResetConfirmRequest;
import com.aivastudio.model.entity.PasswordResetToken;
import com.aivastudio.model.entity.User;
import com.aivastudio.repository.AuthTokenRepository;
import com.aivastudio.repository.PasswordResetTokenRepository;
import com.aivastudio.repository.UserRepository;

@Service
public class PasswordResetService {

    private static final Duration TOKEN_LIFETIME = Duration.ofMinutes(30);
    private static final Duration REQUEST_COOLDOWN = Duration.ofSeconds(60);

    private final SecureRandom secureRandom = new SecureRandom();
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final AuthTokenRepository authTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetMailService mailService;
    private final String frontendUrl;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository resetTokenRepository,
                                AuthTokenRepository authTokenRepository,
                                PasswordEncoder passwordEncoder,
                                PasswordResetMailService mailService,
                                @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl) {
        this.userRepository = userRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.authTokenRepository = authTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.frontendUrl = frontendUrl;
    }

    /**
     * Genera un enlace de recuperación y lo envía por correo.
     * No lanza error si el correo no existe: el controlador responde igual en ambos casos.
     */
    @Transactional
    public void requestReset(String rawEmail) {
        String email = rawEmail.trim().toLowerCase(Locale.ROOT);
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            LocalDateTime since = LocalDateTime.now().minus(REQUEST_COOLDOWN);
            if (resetTokenRepository.existsByUserIdAndUsedAtIsNullAndFechaCreacionAfter(user.getId(), since)) {
                return; // ya se envió un enlace hace menos de un minuto
            }

            Instant now = Instant.now();
            resetTokenRepository.invalidateAllForUser(user.getId(), now); // solo el enlace más reciente sirve

            String token = generateToken();
            resetTokenRepository.save(new PasswordResetToken(user, hash(token), now.plus(TOKEN_LIFETIME)));

            String link = frontendUrl.replaceAll("/+$", "") + "/restablecer-password?token=" + token;
            mailService.sendResetLink(user.getEmail(), user.getName(), link);
        });
    }

    /** Valida el token, guarda la nueva contraseña y cierra todas las sesiones abiertas de la cuenta. */
    @Transactional
    public void resetPassword(PasswordResetConfirmRequest request) {
        Instant now = Instant.now();
        PasswordResetToken resetToken = resetTokenRepository
                .findByTokenHashAndUsedAtIsNullAndExpiresAtAfter(hash(request.token().trim()), now)
                .orElseThrow(() -> new ReglaDeNegocioException(
                        "El enlace de recuperación no es válido o ya expiró. Solicita uno nuevo."));

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.passwordNueva()));
        userRepository.save(user);

        resetTokenRepository.invalidateAllForUser(user.getId(), now); // el enlace es de un solo uso
        authTokenRepository.revokeAllByUserId(user.getId(), now);     // cierra sesiones activas
    }

    private String generateToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 no está disponible", ex);
        }
    }
}