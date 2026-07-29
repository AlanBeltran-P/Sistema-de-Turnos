package com.SystemT.Turnos.Dto.auth;

import java.util.UUID;

public record ProfesionalResponse(
        UUID publicId,
        String nombre,
        String email,
        String slug,
        String telefono
) {}
