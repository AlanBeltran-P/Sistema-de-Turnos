package com.SystemT.Turnos.Dto.Turno;

import com.SystemT.Turnos.Entity.EstadoTurno;

import java.time.LocalDateTime;
import java.util.UUID;

public record TurnoAgendaResponse(
        UUID publicId,
        String servicioNombre,
        String clienteNombre,
        String clienteTelefono,
        String clienteEmail,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        EstadoTurno estado
) {}