package com.aivastudio.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.aivastudio.model.embeddable.ConfiguracionVideo;
import com.aivastudio.model.entity.Project;

public record ProjectResponse(
        Long id,
        Long usuarioId,
        String nombre,
        String prompt,
        String modelId,
        String modeloIa,
        long creditosDisponibles,
        long costoCreditos,
        int duracionSeg,
        String formato,
        String estilo,
        String estado,
        LocalDateTime fechaCreacion,
        List<String> tags) {

    public static ProjectResponse from(Project project) {
        ConfiguracionVideo config = project.getConfiguracion();
        return new ProjectResponse(
                project.getId(),
                project.getUser().getId(),
                project.getName(),
                project.getPrompt(),
                project.getAiModel().getCodigo(),
                project.getAiModel().getNombre(),
                project.getUser().getCredits(),
                project.getCreditCost(),
                config.getDuracionSegundos(),
                config.getFormatoPantalla().getValor(),
                config.getEstiloVisual(),
                project.getEstado().name(),
                project.getFechaCreacion(),
                project.getTags().stream().map(tag -> tag.getNombre()).sorted().toList());
    }
}