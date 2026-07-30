package com.SystemT.Turnos.Dto.Turno;

import com.SystemT.Turnos.Entity.EstadoTurno;

import java.time.LocalDateTime;
import java.util.UUID;

public record TurnoResponse(
        UUID publicId,
        String servicioNombre,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        EstadoTurno estado,
        UUID cancelacionToken
) {}