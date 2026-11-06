package com.aivastudio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aivastudio.model.entity.Credit;

public interface CreditRepository extends JpaRepository<Credit, Long> {

    // Consulta 1 (por convención de nombre), renombrada por el cambio a BaseEntity
    List<Credit> findAllByUser_IdOrderByFechaCreacionDesc(Long userId);

    // Consulta 2 (@Query con @Param): total acumulado de recargas positivas
    @Query("select coalesce(sum(c.amount), 0) from Credit c "
            + "where c.user.id = :userId and c.amount > 0")
    long totalAcreditadoPorUsuario(@Param("userId") Long userId);
}