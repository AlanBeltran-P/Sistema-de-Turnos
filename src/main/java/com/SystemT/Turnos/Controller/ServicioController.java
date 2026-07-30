package com.SystemT.Turnos.Controller;


import com.SystemT.Turnos.Dto.servicio.ServicioRequest;
import com.SystemT.Turnos.Dto.servicio.ServicioResponse;
import com.SystemT.Turnos.service.ServicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final ServicioService servicioService;

    @PostMapping
    public ResponseEntity<ServicioResponse> crear(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody ServicioRequest request) {
        ServicioResponse response = servicioService.crear(email, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ServicioResponse>> listarPropios(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(servicioService.listarPropios(email));
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<ServicioResponse> actualizar(
            @AuthenticationPrincipal String email,
            @PathVariable UUID publicId,
            @Valid @RequestBody ServicioRequest request) {
        return ResponseEntity.ok(servicioService.actualizar(email, publicId, request));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> desactivar(
            @AuthenticationPrincipal String email,
            @PathVariable UUID publicId) {
        servicioService.desactivar(email, publicId);
        return ResponseEntity.noContent().build();
    }
}
