package com.aivastudio.model.dto;

import java.time.LocalDateTime;

import com.aivastudio.model.entity.Credit;

public record CreditResponse(Long id, String label, long amount, LocalDateTime date) {

    public static CreditResponse from(Credit movement) {
        return new CreditResponse(
                movement.getId(),
                movement.getDescription(),
                movement.getAmount(),
                movement.getCreatedAt());
    }
}