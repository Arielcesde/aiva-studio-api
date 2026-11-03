package com.aivastudio.model.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.aivastudio.model.entity.Credit;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreditResponse(
        Long id,
        String label,
        Long amount,
        LocalDateTime date,
        Long usuarioId,
        Long cantidad,
        Long bonificacion,
        Long totalRecargado,
        Long creditosDisponibles) {

    public static CreditResponse movimiento(Credit credit) {
        return new CreditResponse(
                credit.getId(),
                credit.getDescription(),
                credit.getAmount(),
                credit.getCreatedAt(),
                null, null, null, null, null);
    }

    public static CreditResponse recarga(
            Long usuarioId,
            long cantidad,
            long bonificacion,
            long totalRecargado,
            long creditosDisponibles) {
        return new CreditResponse(
                null, null, null, null,
                usuarioId, cantidad, bonificacion, totalRecargado, creditosDisponibles);
    }
}