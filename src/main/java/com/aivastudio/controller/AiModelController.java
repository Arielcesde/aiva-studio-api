package com.aivastudio.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aivastudio.model.dto.AiModelRequest;
import com.aivastudio.model.dto.AiModelResponse;
import com.aivastudio.service.AiModelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ai-models")
@Tag(name = "AI Models", description = "Catálogo de modelos de IA para generar video")
public class AiModelController {

    private final AiModelService aiModelService;

    public AiModelController(AiModelService aiModelService) {
        this.aiModelService = aiModelService;
    }

    @GetMapping
    @Operation(summary = "Listar los modelos de IA activos", description = "Incluye el costo en créditos por segundo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Modelos disponibles"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
    public List<AiModelResponse> list() {
        return aiModelService.list();
    }

    @PostMapping
    @Operation(summary = "Registrar un modelo de IA")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Modelo creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o modelo duplicado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
    public ResponseEntity<AiModelResponse> create(@Valid @RequestBody AiModelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(aiModelService.create(request));
    }
}