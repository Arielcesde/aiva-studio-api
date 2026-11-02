package com.aivastudio.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aivastudio.exception.RecursoNoEncontradoException;
import com.aivastudio.exception.ReglaNegocioException;
import com.aivastudio.model.dto.ProjectRequest;
import com.aivastudio.model.dto.ProjectResponse;
import com.aivastudio.model.entity.Credit;
import com.aivastudio.model.entity.Project;
import com.aivastudio.model.entity.User;
import com.aivastudio.repository.CreditRepository;
import com.aivastudio.repository.ProjectRepository;
import com.aivastudio.repository.UserRepository;

@Service
public class ProjectService {

    private static final int MAX_NAME_LENGTH = 80;
    private static final int MAX_PROMPT_LENGTH = 1000;
    private static final int MAX_STYLE_LENGTH = 40;
    private static final Set<Integer> SUPPORTED_DURATIONS = Set.of(5, 10, 15, 30, 45, 60);
    private static final Set<String> SUPPORTED_FORMATS = Set.of("16:9", "9:16", "1:1");

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CreditRepository creditMovementRepository;
    private final UserService currentUserService;
    private final AiModelService aiModelService;

    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            CreditRepository creditMovementRepository,
            UserService currentUserService,
            AiModelService aiModelService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.creditMovementRepository = creditMovementRepository;
        this.currentUserService = currentUserService;
        this.aiModelService = aiModelService;
    }

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        User user = lockAndRequireUser(request.usuarioId());
        String name = validateName(request.nombre());
        String prompt = validatePrompt(request.prompt());
        var model = aiModelService.find(request.modelId(), request.modeloIa());
        int duration = validateDuration(request.duracionSeg());
        String format = validateFormat(request.formato());
        String style = request.estilo() == null || request.estilo().isBlank()
                ? "Cinematográfico"
                : request.estilo().trim();
        if (style.length() > MAX_STYLE_LENGTH) {
            throw new ReglaNegocioException("El estilo no puede superar 40 caracteres");
        }

        long cost = Math.multiplyExact((long) model.costoPorSegundo(), duration);
        if (request.costoCreditos() != null && request.costoCreditos() != cost) {
            throw new ReglaNegocioException("El costo enviado no coincide con el costo calculado por el servidor");
        }
        if (user.getCredits() < cost) {
            throw new ReglaNegocioException("Créditos insuficientes para crear este proyecto");
        }

        Project project = projectRepository.save(new Project(
                user,
                name,
                prompt,
                model.id(),
                model.nombre(),
                cost,
                duration,
                format,
                style));
        user.setCredits(user.getCredits() - cost);
        creditMovementRepository.save(new Credit(user, -cost, "Generación · " + name));
        return ProjectResponse.from(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> listForUser(Long userId) {
        currentUserService.requireUser(userId);
        return projectRepository.findAllByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(ProjectResponse::from)
                .toList();
    }

    private User lockAndRequireUser(Long userId) {
        User currentUser = currentUserService.currentUser();
        if (userId == null || !currentUser.getId().equals(userId)) {
            throw new ReglaNegocioException("No puedes operar sobre otra cuenta");
        }
        return userRepository.findLockedById(userId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ReglaNegocioException("El nombre del proyecto es obligatorio");
        }
        String normalized = name.trim();
        if (normalized.length() > MAX_NAME_LENGTH) {
            throw new ReglaNegocioException("El nombre del proyecto no puede superar 80 caracteres");
        }
        return normalized;
    }

    private String validatePrompt(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new ReglaNegocioException("La descripción del proyecto es obligatoria");
        }
        String normalized = prompt.trim();
        if (normalized.length() > MAX_PROMPT_LENGTH) {
            throw new ReglaNegocioException("El prompt no puede superar 1000 caracteres");
        }
        return normalized;
    }

    private int validateDuration(Integer duration) {
        if (duration == null || !SUPPORTED_DURATIONS.contains(duration)) {
            throw new ReglaNegocioException("La duración debe ser 5, 10, 15, 30, 45 o 60 segundos");
        }
        return duration;
    }

    private String validateFormat(String format) {
        String normalized = format == null || format.isBlank() ? "16:9" : format.trim();
        if (!SUPPORTED_FORMATS.contains(normalized)) {
            throw new ReglaNegocioException("El formato debe ser 16:9, 9:16 o 1:1");
        }
        return normalized;
    }
}
