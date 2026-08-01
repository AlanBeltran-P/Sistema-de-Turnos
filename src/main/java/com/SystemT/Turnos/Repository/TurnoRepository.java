package com.SystemT.Turnos.Repository;


import com.SystemT.Turnos.Entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    Optional<Turno> findByPublicId(UUID publicId);

    Optional<Turno> findByCancelacionToken(UUID cancelacionToken);

    List<Turno> findByProfesionalIdAndFechaHoraInicioBetween(
            Long profesionalId, LocalDateTime desde, LocalDateTime hasta
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Turno t WHERE t.profesional.id = :profesionalId " +
            "AND t.fechaHoraInicio < :hasta AND t.fechaHoraFin > :desde " +
            "AND t.estado <> 'CANCELADO'")
    List<Turno> findOcupadosEnRangoConLock(Long profesionalId, LocalDateTime desde, LocalDateTime hasta);

    @Query("SELECT t FROM Turno t WHERE t.profesional.id = :profesionalId " +
            "AND t.fechaHoraInicio < :hasta AND t.fechaHoraFin > :desde " +
            "AND t.estado <> 'CANCELADO'")
    List<Turno> findOcupadosEnRango(Long profesionalId, LocalDateTime desde, LocalDateTime hasta);

}
