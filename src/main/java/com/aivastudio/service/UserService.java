package com.aivastudio.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.aivastudio.exception.RecursoNoEncontradoException;
import com.aivastudio.exception.ReglaDeNegocioException;
import com.aivastudio.model.entity.User;
import com.aivastudio.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
