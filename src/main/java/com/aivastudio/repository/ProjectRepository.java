package com.aivastudio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa .repository.JpaRepository;

import com.aivastudio.model.entity.Project;
import com.aivastudio.model.enums.EstadoProyecto;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Consulta 1 (por convención de nombre)
    List<Project> findAllByUser_IdOrderByFechaCreacionDesc(Long userId);

    // Consulta 2 (por convención de nombre): filtra por estado del proyecto
    List<Project> findAllByUser_IdAndEstadoOrderByFechaCreacionDesc(Long userId, EstadoProyecto estado);

    // Busca un proyecto solo si pertenece al usuario indicado
    Optional<Project> findByIdAndUser_Id(Long projectId, Long userId);
}