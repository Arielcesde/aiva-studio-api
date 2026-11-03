package com.aivastudio.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.aivastudio.exception.ReglaNegocioException;
import com.aivastudio.model.dto.AiModelResponse;

@Service
public class AiModelService {

    private final List<AiModelResponse> models = List.of(
            new AiModelResponse("lumina-v2", "Lumina Video Pro v2", 2, "Equilibrado",
                    "Una opción versátil para anuncios, historias y contenido diario.", "coral"),
            new AiModelResponse("sora-api", "Sora API Direct", 5, "Alta fidelidad",
                    "Escenas detalladas y narrativas con acabado cinematográfico.", "blue"),
            new AiModelResponse("runway-gen3", "Runway Gen-3 Alpha", 4, "Creativo",
                    "Ideal para explorar estilos, movimiento y conceptos visuales.", "gold"),
            new AiModelResponse("svd-xl", "Stable Video Diffusion XL", 1, "Ligero",
                    "Prueba ideas y conceptos con un consumo de créditos menor.", "green"));

    private final Map<String, AiModelResponse> modelsById = models.stream()
            .collect(java.util.stream.Collectors.toUnmodifiableMap(model -> model.id(), model -> model));

    public List<AiModelResponse> list() {
        return models;
    }

    public AiModelResponse find(String id, String name) {
        AiModelResponse model = id == null ? null : modelsById.get(id);
        if (model == null && name != null) {
            model = models.stream()
                    .filter(candidate -> candidate.nombre().equalsIgnoreCase(name.trim()))
                    .findFirst()
                    .orElse(null);
        }
        if (model == null) {
            throw new ReglaNegocioException("Selecciona un modelo de IA válido");
        }
        return model;
    }
}
