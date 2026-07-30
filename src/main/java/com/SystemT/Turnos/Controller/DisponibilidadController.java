package com.SystemT.Turnos.Controller;


import com.SystemT.Turnos.Dto.disponibilidad.DisponibilidadRequest;
import com.SystemT.Turnos.Dto.disponibilidad.DisponibilidadResponse;
import com.SystemT.Turnos.service.DisponibilidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disponibilidad")
@RequiredArgsConstructor
public class DisponibilidadController {

    private final DisponibilidadService disponibilidadService;

    @PostMapping
    public ResponseEntity<DisponibilidadResponse> crear(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody DisponibilidadRequest request) {
        DisponibilidadResponse response = disponibilidadService.crear(email, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DisponibilidadResponse>> listarPropias(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(disponibilidadService.listarPropias(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadResponse> actualizar(
            @AuthenticationPrincipal String email,
            @PathVariable Long id,
            @Valid @RequestBody DisponibilidadRequest request) {
        return ResponseEntity.ok(disponibilidadService.actualizar(email, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        disponibilidadService.eliminar(email, id);
        return ResponseEntity.noContent().build();
    }
}
