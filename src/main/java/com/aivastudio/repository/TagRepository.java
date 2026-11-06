package com.aivastudio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aivastudio.model.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}