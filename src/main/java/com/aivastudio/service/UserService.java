package com.aivastudio.service;

import java.util.Locale;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aivastudio.exception.RecursoNoEncontradoException;
import com.aivastudio.exception.ReglaDeNegocioException;
import com.aivastudio.model.dto.PasswordChangeRequest;
import com.aivastudio.model.dto.UserResponse;
import com.aivastudio.model.dto.UserUpdateRequest;
import com.aivastudio.model.entity.User;
import com.aivastudio.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User requireUser(Long userId) {
        if (userId == null) {
            throw new ReglaDeNegocioException("El usuario es obligatorio");
        }
        if (!currentUser().getId().equals(userId)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ReglaDeNegocioException("La sesión no es válida");
        }
        return user;
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UserUpdateRequest request) {
        User user = requireUser(userId);
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        boolean emailChanged = !email.equalsIgnoreCase(user.getEmail());
        if (emailChanged && userRepository.existsByEmailIgnoreCase(email)) {
            throw new ReglaDeNegocioException("Ya existe una cuenta con ese correo electrónico");
        }

        user.setName(request.nombre().trim());
        user.setEmail(email);
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = requireUser(userId);
        if (!passwordEncoder.matches(request.passwordActual(), user.getPasswordHash())) {
            // Debe ser 400 (ReglaDeNegocio) y NO 401: el frontend cierra la sesión ante un 401.
            throw new ReglaDeNegocioException("La contraseña actual no es correcta");
        }
        user.setPasswordHash(passwordEncoder.encode(request.passwordNueva()));
        userRepository.save(user);
    }
}