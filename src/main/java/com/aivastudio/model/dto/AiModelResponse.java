package com.aivastudio.model.dto;

import java.math.BigDecimal;

import com.aivastudio.model.entity.AiModel;

public record AiModelResponse(
        String id,
        String nombre,
        BigDecimal costoPorSegundo,
        String categoria,
        String descripcion,
        String color) {

    public static AiModelResponse from(AiModel model) {
        return new AiModelResponse(
                model.getCodigo(),
                model.getNombre(),
                model.getCostoPorSegundo(),
                model.getCategoria(),
                model.getDescripcion(),
                model.getColor());
    }
}