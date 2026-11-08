package com.aivastudio.repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aivastudio.model.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    @EntityGraph(attributePaths = "user")
    Optional<PasswordResetToken> findByTokenHashAndUsedAtIsNullAndExpiresAtAfter(String tokenHash, Instant now);

    // Evita que alguien dispare decenas de correos seguidos a una misma cuenta.
    boolean existsByUserIdAndUsedAtIsNullAndFechaCreacionAfter(Long userId, LocalDateTime since);

    @Modifying
    @Query("update PasswordResetToken token set token.usedAt = :now "
            + "where token.user.id = :userId and token.usedAt is null")
    int invalidateAllForUser(@Param("userId") Long userId, @Param("now") Instant now);
}