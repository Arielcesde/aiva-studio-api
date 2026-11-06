package com.aivastudio.model.dto;

import com.aivastudio.model.entity.User;

public record UserResponse(
        Long id,
        String nombre,
        String email,
        long creditosDisponibles,
        boolean suscripcionActiva,
        String rol) {

    public static UserResponse from(User user) {
        boolean suscripcionActiva = user.getSubscription() != null
                && user.getSubscription().isActiva();
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCredits(),
                suscripcionActiva,
                user.getRol().name());
    }
}

