package com.aivastudio.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.aivastudio.exception.RecursoNoEncontradoException;
import com.aivastudio.exception.ReglaNegocioException;
import com.aivastudio.model.entity.User;
import com.aivastudio.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User requireUser(Long requestedId) {
        User currentUser = currentUser();
        if (requestedId == null || !currentUser.getId().equals(requestedId)) {
            throw new ReglaNegocioException("No puedes operar sobre otra cuenta");
        }
        return userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ReglaNegocioException("La sesión no es válida");
        }
        return user;
    }
}
