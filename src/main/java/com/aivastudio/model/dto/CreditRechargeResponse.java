package com.aivastudio.model.dto;

import java.time.LocalDateTime;

public record CreditRechargeResponse(
        Long id,
        String label,
        Long amount,
        LocalDateTime date,
        Long usuarioId,
        Long cantidad,
        Long bonificacion,
        Long totalRecargado,
        Long creditosDisponibles) {
}