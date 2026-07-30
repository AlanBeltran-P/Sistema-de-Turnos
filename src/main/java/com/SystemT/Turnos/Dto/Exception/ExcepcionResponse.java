package com.SystemT.Turnos.Dto.Exception;

import java.time.LocalDate;
import java.time.LocalTime;

public record ExcepcionResponse(
        Long id,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        String motivo
) {}