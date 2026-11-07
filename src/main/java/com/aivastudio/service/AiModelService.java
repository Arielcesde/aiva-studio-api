package com.aivastudio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aivastudio.exception.RecursoNoEncontradoException;
import com.aivastudio.exception.ReglaDeNegocioException;
import com.aivastudio.model.dto.AiModelRequest;
import com.aivastudio.model.dto.AiModelResponse;
import com.aivastudio.model.entity.AiModel;
import com.aivastudio.repository.AiModelRepository;

@Service
public class AiModelService {

    private final AiModelRepository aiModelRepository;

    public AiModelService(AiModelRepository aiModelRepository) {
        this.aiModelRepository = aiModelRepository;
    }

    @Transactional(readOnly = true)
    public List<AiModelResponse> list() {
        return aiModelRepository.findByActivoTrue()
                .stream()
                .map(AiModelResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AiModel findActiveByCodigo(String codigo) {
        return aiModelRepository.findByCodigoAndActivoTrue(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El modelo de IA '" + codigo + "' no existe o no está disponible"));
    }

    @Transactional
    public AiModelResponse create(AiModelRequest request) {
        if (aiModelRepository.existsByCodigoIgnoreCase(request.codigo())) {
            throw new ReglaDeNegocioException("Ya existe un modelo con el código " + request.codigo());
        }
        if (aiModelRepository.existsByNombreIgnoreCaseAndVersionIgnoreCase(
                request.nombre(), request.version())) {
            throw new ReglaDeNegocioException("Ya existe ese modelo en esa versión");
        }
        AiModel model = new AiModel(
                request.codigo().trim().toLowerCase(),
                request.nombre().trim(),
                request.proveedor().trim(),
                request.version().trim(),
                request.costoPorSegundo(),
                request.categoria().trim(),
                request.descripcion().trim(),
                request.color().trim());
        if (request.activo() != null) {
            model.setActivo(request.activo());
        }
        return AiModelResponse.from(aiModelRepository.save(model));
    }
}