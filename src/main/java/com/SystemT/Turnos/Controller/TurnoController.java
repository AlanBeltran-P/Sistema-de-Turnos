package com.SystemT.Turnos.Controller;


import com.SystemT.Turnos.Dto.Turno.CambiarEstadoRequest;
import com.SystemT.Turnos.Dto.Turno.TurnoAgendaResponse;
import com.SystemT.Turnos.service.TurnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    @GetMapping("/agenda")
    public ResponseEntity<List<TurnoAgendaResponse>> verAgenda(
            @AuthenticationPrincipal String email,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(turnoService.verAgenda(email, desde, hasta));
    }

    @PatchMapping("/{publicId}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @AuthenticationPrincipal String email,
            @PathVariable UUID publicId,
            @Valid @RequestBody CambiarEstadoRequest request) {
        turnoService.cambiarEstado(email, publicId, request.nuevoEstado());
        return ResponseEntity.noContent().build();
    }
}
