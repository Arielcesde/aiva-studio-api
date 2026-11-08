package com.aivastudio.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aivastudio.model.dto.ProjectRequest;
import com.aivastudio.model.dto.ProjectResponse;
import com.aivastudio.model.enums.EstadoProyecto;
import com.aivastudio.service.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "Projects", description = "Creación y consulta de proyectos de video generados con IA")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/projects")
    @Operation(summary = "Crear un proyecto de video",
            description = "Calcula el costo según el modelo y la duración, y descuenta los créditos del usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Proyecto creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o créditos insuficientes"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "404", description = "Modelo de IA no encontrado")
    })
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(request));
    }

    @GetMapping("/users/{userId}/projects")
    @Operation(summary = "Listar los proyectos del usuario",
            description = "Opcionalmente filtra por estado: EN_COLA, BORRADOR, EN_PROCESO, COMPLETADO o ARCHIVADO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Proyectos del más reciente al más antiguo"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public List<ProjectResponse> listByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) EstadoProyecto estado) {
        return projectService.listForUser(userId, estado);
    }

        @DeleteMapping("/projects/{projectId}")
    @Operation(summary = "Eliminar un proyecto del usuario autenticado",
            description = "Funciona en cualquier estado. Los créditos usados no se devuelven.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Proyecto eliminado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long projectId) {
        projectService.delete(projectId);
        return ResponseEntity.noContent().build();
    }
}