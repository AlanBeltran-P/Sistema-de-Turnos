package com.SystemT.Turnos.Dto.Turno;

import com.SystemT.Turnos.Entity.EstadoTurno;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoRequest(@NotNull EstadoTurno nuevoEstado) {}
