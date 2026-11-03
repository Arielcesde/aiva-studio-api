package com.aivastudio.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreditRechargeRequest(
        @NotNull(message = "No puedes operar sobre otra cuenta")
        Long usuarioId,
        @NotNull(message = "La recarga mínima es de 50 créditos")
        @Min(value = 50, message = "La recarga mínima es de 50 créditos")
        Long cantidad) {
}