package com.SystemT.Turnos.Dto.disponibilidad;
import java.time.LocalTime;

public record DisponibilidadResponse(
        Long id,
        Integer diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin
){}