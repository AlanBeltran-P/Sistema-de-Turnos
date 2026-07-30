package com.SystemT.Turnos.Dto.servicio;

import java.math.BigDecimal;
import java.util.UUID;

public record ServicioResponse(
        UUID publicId,
        String nombre,
        Integer duracionMin,
        BigDecimal precio,
        boolean activo
) {}