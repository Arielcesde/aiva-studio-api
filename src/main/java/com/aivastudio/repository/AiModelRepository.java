package com.aivastudio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aivastudio.model.entity.AiModel;

public interface AiModelRepository extends JpaRepository<AiModel, Long> {

    List<AiModel> findByActivoTrue();

    Optional<AiModel> findByCodigoAndActivoTrue(String codigo);

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByNombreIgnoreCaseAndVersionIgnoreCase(String nombre, String version);

    boolean existsByNombreIgnoreCaseAndVersionIgnoreCaseAndIdNot(String nombre, String version, Long id);
}