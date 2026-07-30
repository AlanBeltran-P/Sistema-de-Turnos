package com.SystemT.Turnos.Controller;

import com.SystemT.Turnos.Dto.Exception.ExcepcionRequest;
import com.SystemT.Turnos.Dto.Exception.ExcepcionResponse;
import com.SystemT.Turnos.service.ExcepcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/excepciones")
@RequiredArgsConstructor
public class ExcepcionController {

    private final ExcepcionService excepcionService;

    @PostMapping
    public ResponseEntity<ExcepcionResponse> crear(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody ExcepcionRequest request) {
        ExcepcionResponse response = excepcionService.crear(email, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ExcepcionResponse>> listarPropias(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(excepcionService.listarPropias(email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        excepcionService.eliminar(email, id);
        return ResponseEntity.noContent().build();
    }
}