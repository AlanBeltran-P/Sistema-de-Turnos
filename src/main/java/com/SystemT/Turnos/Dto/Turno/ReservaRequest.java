package com.SystemT.Turnos.Dto.Turno;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservaRequest(
        @NotNull UUID servicioPublicId,
        @NotNull @FutureOrPresent LocalDateTime fechaHoraInicio,
        @NotBlank String clienteNombre,
        @NotBlank String clienteTelefono,
        @Email String clienteEmail
) {}