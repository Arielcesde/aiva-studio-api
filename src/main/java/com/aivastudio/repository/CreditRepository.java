package com.aivastudio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aivastudio.model.entity.Credit;

public interface CreditRepository extends JpaRepository<Credit, Long> {

    List<Credit> findAllByUser_IdOrderByCreatedAtDesc(Long userId);
}