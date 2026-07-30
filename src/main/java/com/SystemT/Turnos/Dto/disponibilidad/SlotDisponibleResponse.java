package com.SystemT.Turnos.Dto.disponibilidad;

import java.time.LocalTime;

public record SlotDisponibleResponse(LocalTime horaInicio, LocalTime horaFin) {}
