package com.SystemT.Turnos.Controller;

import com.SystemT.Turnos.Dto.Turno.ReservaRequest;
import com.SystemT.Turnos.Dto.Turno.TurnoResponse;
import com.SystemT.Turnos.Dto.disponibilidad.SlotDisponibleResponse;
import com.SystemT.Turnos.Dto.servicio.ServicioResponse;
import com.SystemT.Turnos.Entity.Servicio;
import com.SystemT.Turnos.Exception.ResourceNotFoundException;
import com.SystemT.Turnos.Repository.ProfesionalRepository;
import com.SystemT.Turnos.Repository.ServicioRepository;
import com.SystemT.Turnos.service.DisponibilidadCalculatorService;
import com.SystemT.Turnos.service.ServicioService;
import com.SystemT.Turnos.service.TurnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final ServicioService servicioService;
    private final ServicioRepository servicioRepository;
    private final ProfesionalRepository profesionalRepository;
    private final DisponibilidadCalculatorService disponibilidadCalculatorService;
    private final TurnoService turnoService;

    @GetMapping("/profesionales/{slug}/servicios")
    public ResponseEntity<List<ServicioResponse>> listarServicios(@PathVariable String slug) {
        return ResponseEntity.ok(servicioService.listarPublicos(slug));
    }

    @GetMapping("/profesionales/{slug}/disponibilidad")
    public ResponseEntity<List<SlotDisponibleResponse>> consultarDisponibilidad(
            @PathVariable String slug,
            @RequestParam UUID servicioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        Long profesionalId = profesionalRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Profesional no encontrado"))
                .getId();

        Servicio servicio = servicioRepository
                .findByPublicIdAndProfesional_SlugAndActivoTrue(servicioId, slug)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        List<SlotDisponibleResponse> slots =
                disponibilidadCalculatorService.calcularSlots(profesionalId, servicio, fecha);

        return ResponseEntity.ok(slots);
    }

    @PostMapping("/profesionales/{slug}/turnos")
    public ResponseEntity<TurnoResponse> reservar(
            @PathVariable String slug,
            @Valid @RequestBody ReservaRequest request) {
        TurnoResponse response = turnoService.reservar(slug, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/turnos/{cancelacionToken}")
    public ResponseEntity<Void> cancelar(@PathVariable UUID cancelacionToken) {
        turnoService.cancelar(cancelacionToken);
        return ResponseEntity.noContent().build();
    }
}
