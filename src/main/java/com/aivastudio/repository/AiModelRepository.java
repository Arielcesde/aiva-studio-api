package com.aivastudio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aivastudio.model.entity.AiModel;

public interface AiModelRepository extends JpaRepository<AiModel, Long> {
    List<AiModel> findByActivoTrue();
    boolean existsByNombreIgnoreCaseAndVersionIgnoreCase(String nombre, String version);
    boolean existsByNombreIgnoreCaseAndVersionIgnoreCaseAndIdNot(String nombre, String version, Long id);
}