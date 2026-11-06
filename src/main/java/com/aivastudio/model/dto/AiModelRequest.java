package com.aivastudio.model.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AiModelRequest(
        @NotBlank(message = "El nombre del modelo es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @NotBlank(message = "El proveedor es obligatorio")
        @Size(max = 100, message = "El proveedor no puede superar 100 caracteres")
        String proveedor,

        @NotBlank(message = "La versión es obligatoria")
        @Size(max = 30, message = "La versión no puede superar 30 caracteres")
        String version,

        @NotNull(message = "El costo por segundo es obligatorio")
        @DecimalMin(value = "0.0001", message = "El costo por segundo debe ser mayor que cero")
        @Digits(integer = 6, fraction = 4, message = "El costo por segundo admite hasta 4 decimales")
        BigDecimal costoPorSegundo,

        @NotBlank(message = "El código del modelo es obligatorio")
        @Size(max = 50) String codigo,

        @NotBlank(message = "La categoría es obligatoria")
        @Size(max = 50) String categoria,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 300) String descripcion,

        @NotBlank(message = "El color es obligatorio")
        @Size(max = 20) String color,

        Boolean activo) {
}

