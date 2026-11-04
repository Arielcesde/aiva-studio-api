package com.aivastudio.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aivastudio.exception.CredencialesInvalidasException;
import com.aivastudio.exception.ReglaNegocioException;
import com.aivastudio.model.dto.AuthResponse;
import com.aivastudio.model.dto.LoginRequest;
import com.aivastudio.model.dto.UserResponse;
import com.aivastudio.model.entity.Credit;
import com.aivastudio.model.entity.User;
import com.aivastudio.repository.CreditRepository;
import com.aivastudio.repository.UserRepository;

@Service
public class AuthService {

    public record UserRequest(String nombre, String email, String password) {
    }

    private static final long INITIAL_CREDITS = 100;

    private final UserRepository userRepository;
    private final CreditRepository creditRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;

    public AuthService(UserRepository userRepository,
                       CreditRepository creditRepository,
                       PasswordEncoder passwordEncoder,
                       AuthTokenService authTokenService) {
        this.userRepository = userRepository;
        this.creditRepository = creditRepository;
        this.passwordEncoder = passwordEncoder;
        this.authTokenService = authTokenService;
    }

    @Transactional
    public AuthResponse register(UserRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String password = request.password();
        if (!email.contains("@")) {
            throw new ReglaNegocioException("El correo electrónico no es válido");
        }
        if (password.length() < 6 || password.length() > 72) {
            throw new ReglaNegocioException("La contraseña debe tener entre 6 y 72 caracteres");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ReglaNegocioException("Ya existe una cuenta con ese correo electrónico");
        }
        User user = userRepository.save(new User(
                request.nombre().trim(), email, passwordEncoder.encode(password), INITIAL_CREDITS));
        creditRepository.save(new Credit(user, INITIAL_CREDITS, "Créditos iniciales"));
        return new AuthResponse(authTokenService.issue(user), UserResponse.from(user));
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(CredencialesInvalidasException::new);
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }
        return new AuthResponse(authTokenService.issue(user), UserResponse.from(user));
    }

    public void logout(String token) {
        authTokenService.revoke(token);
    }
}