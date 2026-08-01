package com.SystemT.Turnos.service;

import com.SystemT.Turnos.Dto.Turno.ReservaRequest;
import com.SystemT.Turnos.Dto.Turno.TurnoAgendaResponse;
import com.SystemT.Turnos.Dto.Turno.TurnoResponse;
import com.SystemT.Turnos.Entity.EstadoTurno;
import com.SystemT.Turnos.Entity.Profesional;
import com.SystemT.Turnos.Entity.Servicio;
import com.SystemT.Turnos.Entity.Turno;
import com.SystemT.Turnos.Exception.ResourceNotFoundException;
import com.SystemT.Turnos.Exception.SlotNoDisponibleException;
import com.SystemT.Turnos.Repository.ProfesionalRepository;
import com.SystemT.Turnos.Repository.ServicioRepository;
import com.SystemT.Turnos.Repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final ServicioRepository servicioRepository;
    private final ProfesionalRepository profesionalRepository;

    @Transactional
    public TurnoResponse reservar(String slug, ReservaRequest request) {
        Profesional profesional = profesionalRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Profesional no encontrado"));

        Servicio servicio = servicioRepository
                .findByPublicIdAndProfesional_SlugAndActivoTrue(request.servicioPublicId(), slug)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        LocalDateTime inicio = request.fechaHoraInicio();
        LocalDateTime fin = inicio.plusMinutes(servicio.getDuracionMin());


        List<Turno> ocupados = turnoRepository.findOcupadosEnRangoConLock(profesional.getId(), inicio, fin);
        if (!ocupados.isEmpty()) {
            throw new SlotNoDisponibleException("Ese horario ya no está disponible, elegí otro");
        }

        Turno turno = Turno.builder()
                .profesional(profesional)
                .servicio(servicio)
                .clienteNombre(request.clienteNombre())
                .clienteTelefono(request.clienteTelefono())
                .clienteEmail(request.clienteEmail())
                .fechaHoraInicio(inicio)
                .fechaHoraFin(fin)
                .estado(EstadoTurno.PENDIENTE)
                .build();

        Turno guardado = turnoRepository.save(turno);
        return toResponse(guardado);
    }

    public void cancelar(UUID cancelacionToken) {
        Turno turno = turnoRepository.findByCancelacionToken(cancelacionToken)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado"));

        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new IllegalArgumentException("Este turno ya estaba cancelado");
        }

        turno.setEstado(EstadoTurno.CANCELADO);
        turnoRepository.save(turno);
    }

    private TurnoResponse toResponse(Turno turno) {
        return new TurnoResponse(
                turno.getPublicId(),
                turno.getServicio().getNombre(),
                turno.getFechaHoraInicio(),
                turno.getFechaHoraFin(),
                turno.getEstado(),
                turno.getCancelacionToken()
        );
    }

    public List<TurnoAgendaResponse> verAgenda(String emailProfesional, LocalDate desde, LocalDate hasta) {
        Profesional profesional = profesionalRepository.findByEmail(emailProfesional)
                .orElseThrow(() -> new ResourceNotFoundException("Profesional no encontrado"));

        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(LocalTime.MAX);

        return turnoRepository.findByProfesionalIdAndFechaHoraInicioBetween(profesional.getId(), inicio, fin)
                .stream()
                .map(this::toAgendaResponse)
                .toList();
    }

    @Transactional
    public void cambiarEstado(String emailProfesional, UUID publicId, EstadoTurno nuevoEstado) {
        Turno turno = turnoRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado"));

        if (!turno.getProfesional().getEmail().equals(emailProfesional)) {
            throw new AccessDeniedException("No tenés permiso para modificar este turno");
        }

        turno.setEstado(nuevoEstado);
        turnoRepository.save(turno);
    }

    private TurnoAgendaResponse toAgendaResponse(Turno turno) {
        return new TurnoAgendaResponse(
                turno.getPublicId(),
                turno.getServicio().getNombre(),
                turno.getClienteNombre(),
                turno.getClienteTelefono(),
                turno.getClienteEmail(),
                turno.getFechaHoraInicio(),
                turno.getFechaHoraFin(),
                turno.getEstado()
        );
    }
}
