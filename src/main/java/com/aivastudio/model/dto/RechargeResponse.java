package com.aivastudio.model.dto;

public record RechargeResponse(
        Long usuarioId,
        long cantidad,
        long bonificacion,
        long totalRecargado,
        long creditosDisponibles) {
}
