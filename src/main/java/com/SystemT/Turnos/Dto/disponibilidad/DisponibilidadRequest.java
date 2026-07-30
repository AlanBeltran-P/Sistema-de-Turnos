package com.SystemT.Turnos.Dto.disponibilidad;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record DisponibilidadRequest(
        @NotNull @Min(0) @Max(6) Integer diaSemana,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFin
) {}
