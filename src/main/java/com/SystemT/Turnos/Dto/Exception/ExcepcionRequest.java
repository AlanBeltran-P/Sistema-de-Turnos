package com.SystemT.Turnos.Dto.Exception;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ExcepcionRequest(
        @NotNull @FutureOrPresent LocalDate fecha,
        LocalTime horaInicio,  // null = bloquea el día completo
        LocalTime horaFin,     // null = bloquea el día completo
        String motivo
) {}