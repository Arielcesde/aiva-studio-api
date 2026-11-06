package com.aivastudio.model.dto;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank(message = "El nombre del proyecto es obligatorio")
        @Size(max = 80, message = "El nombre del proyecto no puede superar 80 caracteres")
        String nombre,

        @NotBlank(message = "La descripción del proyecto es obligatoria")
        @Size(max = 1000, message = "El prompt no puede superar 1000 caracteres")
        String prompt,

        @NotBlank(message = "Selecciona un modelo de IA válido")
        String modelId,

        @NotNull(message = "La duración es obligatoria")
        @Min(value = 5, message = "La duración mínima es de 5 segundos")
        @Max(value = 60, message = "La duración máxima es de 60 segundos")
        Integer duracionSeg,

        @Pattern(regexp = "16:9|9:16|1:1", message = "El formato debe ser 16:9, 9:16 o 1:1")
        String formato,

        @Size(max = 40, message = "El estilo no puede superar 40 caracteres")
        String estilo,

        List<@NotBlank @Size(max = 40) String> tags) {
}
