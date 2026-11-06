package com.aivastudio.model.dto;

import java.time.LocalDateTime;

import com.aivastudio.model.entity.Credit;

public record CreditMovementResponse(
        Long id,
        String label,
        Long amount,
        LocalDateTime date,
        Long usuarioId) {

    public static CreditMovementResponse from(Credit credit) {
        return new CreditMovementResponse(
                credit.getId(),
                credit.getDescription(),
                credit.getAmount(),
                credit.getFechaCreacion(),
                credit.getUser().getId());
    }
}