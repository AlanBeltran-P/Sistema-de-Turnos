package com.SystemT.Turnos.Dto.servicio;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ServicioRequest(
        @NotBlank String nombre,
        @NotNull @Positive Integer duracionMin,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal precio
) {}