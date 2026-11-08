package com.aivastudio.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aivastudio.exception.RecursoNoEncontradoException;
import com.aivastudio.exception.ReglaDeNegocioException;
import com.aivastudio.model.dto.ProjectRequest;
import com.aivastudio.model.dto.ProjectResponse;
import com.aivastudio.model.embeddable.ConfiguracionVideo;
import com.aivastudio.model.entity.AiModel;
import com.aivastudio.model.entity.Credit;
import com.aivastudio.model.entity.Project;
import com.aivastudio.model.entity.Tag;
import com.aivastudio.model.entity.User;
import com.aivastudio.model.enums.EstadoProyecto;
import com.aivastudio.model.enums.FormatoPantalla;
import com.aivastudio.repository.CreditRepository;
import com.aivastudio.repository.ProjectRepository;
import com.aivastudio.repository.TagRepository;
import com.aivastudio.repository.UserRepository;

@Service
public class ProjectService {

    private static final Set<Integer> SUPPORTED_DURATIONS = Set.of(5, 10, 15, 30, 45, 60);

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CreditRepository creditMovementRepository;
    private final UserService currentUserService;
    private final AiModelService aiModelService;
    private final TagRepository tagRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            CreditRepository creditMovementRepository,
            UserService currentUserService,
            AiModelService aiModelService,
            TagRepository tagRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.creditMovementRepository = creditMovementRepository;
        this.currentUserService = currentUserService;
        this.aiModelService = aiModelService;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        User user = lockAndRequireUser(currentUserService.currentUser().getId());

        AiModel aiModel = aiModelService.findActiveByCodigo(request.modelId());
        int duration = validateDuration(request.duracionSeg());
        String format = request.formato() == null || request.formato().isBlank()
                ? "16:9"
                : request.formato();
        String style = request.estilo() == null || request.estilo().isBlank()
                ? "Cinematográfico"
                : request.estilo().trim();

        long cost = calculateCost(aiModel, duration);
        if (user.getCredits() < cost) {
            throw new ReglaDeNegocioException("Créditos insuficientes para crear este proyecto");
        }

        ConfiguracionVideo configuracion = new ConfiguracionVideo(
                style, FormatoPantalla.fromValor(format), "1080p", duration);

        Project project = new Project(
                user,
                aiModel,
                request.nombre().trim(),
                request.prompt().trim(),
                cost,
                configuracion);

        if (request.tags() != null) {
            for (String nombre : request.tags()) {
                String limpio = nombre.trim();
                Tag tag = tagRepository.findByNombreIgnoreCase(limpio)
                        .orElseGet(() -> tagRepository.save(new Tag(limpio)));
                project.addTag(tag);
            }
        }

        project = projectRepository.save(project);
        user.setCredits(user.getCredits() - cost);
        creditMovementRepository.save(new Credit(user, -cost, "Generación · " + project.getName()));
        return ProjectResponse.from(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> listForUser(Long userId, EstadoProyecto estado) {
        currentUserService.requireUser(userId);
        List<Project> projects = (estado == null)
                ? projectRepository.findAllByUser_IdOrderByFechaCreacionDesc(userId)
                : projectRepository.findAllByUser_IdAndEstadoOrderByFechaCreacionDesc(userId, estado);
        return projects.stream().map(ProjectResponse::from).toList();
    }

    @Transactional
    public void delete(long projectId){
        long userId = currentUserService.currentUser().getId();
        Project project = projectRepository.findByIdAndUser_Id(projectId, userId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado"));
        projectRepository.delete(project);
    }

    private User lockAndRequireUser(Long userId) {
        if (userId == null) {
            throw new ReglaDeNegocioException("El usuario es obligatorio");
        }
        currentUserService.requireUser(userId);
        return userRepository.findLockedById(userId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    private long calculateCost(AiModel aiModel, int duration) {
        try {
            return aiModel.getCostoPorSegundo()
                    .multiply(BigDecimal.valueOf(duration))
                    .setScale(0, RoundingMode.CEILING)
                    .longValueExact();
        } catch (ArithmeticException ex) {
            throw new ReglaDeNegocioException("El costo del proyecto supera el máximo permitido");
        }
    }

    private int validateDuration(Integer duration) {
        if (duration == null || !SUPPORTED_DURATIONS.contains(duration)) {
            throw new ReglaDeNegocioException("La duración debe ser 5, 10, 15, 30, 45 o 60 segundos");
        }
        return duration;
    }
}