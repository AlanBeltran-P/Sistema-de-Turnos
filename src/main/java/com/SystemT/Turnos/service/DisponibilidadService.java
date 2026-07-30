package com.SystemT.Turnos.service;


import com.SystemT.Turnos.Dto.disponibilidad.DisponibilidadRequest;
import com.SystemT.Turnos.Dto.disponibilidad.DisponibilidadResponse;
import com.SystemT.Turnos.Entity.Disponibilidad;
import com.SystemT.Turnos.Entity.Profesional;
import com.SystemT.Turnos.Mapper.DisponibilidadMapper;
import com.SystemT.Turnos.Repository.DisponibilidadRepository;
import com.SystemT.Turnos.Repository.ProfesionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisponibilidadService {

    private final DisponibilidadRepository disponibilidadRepository;
    private final ProfesionalRepository profesionalRepository;
    private final DisponibilidadMapper disponibilidadMapper;

    public DisponibilidadResponse crear(String emailProfesional, DisponibilidadRequest request) {
        validarHorario(request);
        Profesional profesional = buscarProfesional(emailProfesional);
        validarSinSuperposicion(profesional.getId(), request, null);

        Disponibilidad disponibilidad = disponibilidadMapper.toEntity(request);
        disponibilidad.setProfesional(profesional);

        Disponibilidad guardada = disponibilidadRepository.save(disponibilidad);
        return disponibilidadMapper.toResponse(guardada);
    }

    public List<DisponibilidadResponse> listarPropias(String emailProfesional) {
        Profesional profesional = buscarProfesional(emailProfesional);
        return disponibilidadRepository.findByProfesionalId(profesional.getId())
                .stream()
                .map(disponibilidadMapper::toResponse)
                .toList();
    }

    public DisponibilidadResponse actualizar(String emailProfesional, Long id, DisponibilidadRequest request) {
        validarHorario(request);
        Disponibilidad disponibilidad = buscarPropia(emailProfesional, id);
        validarSinSuperposicion(disponibilidad.getProfesional().getId(), request, id);

        disponibilidad.setDiaSemana(request.diaSemana());
        disponibilidad.setHoraInicio(request.horaInicio());
        disponibilidad.setHoraFin(request.horaFin());

        return disponibilidadMapper.toResponse(disponibilidadRepository.save(disponibilidad));
    }

    public void eliminar(String emailProfesional, Long id) {
        Disponibilidad disponibilidad = buscarPropia(emailProfesional, id);
        disponibilidadRepository.delete(disponibilidad);
    }

    private void validarHorario(DisponibilidadRequest request) {
        if (!request.horaInicio().isBefore(request.horaFin())) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
        }
    }

    private void validarSinSuperposicion(Long profesionalId, DisponibilidadRequest request, Long idAIgnorar) {
        List<Disponibilidad> delMismoDia = disponibilidadRepository
                .findByProfesionalIdAndDiaSemana(profesionalId, request.diaSemana());

        boolean haySuperposicion = delMismoDia.stream()
                .filter(d -> !d.getId().equals(idAIgnorar))
                .anyMatch(d ->
                        request.horaInicio().isBefore(d.getHoraFin()) &&
                                d.getHoraInicio().isBefore(request.horaFin())
                );

        if (haySuperposicion) {
            throw new IllegalArgumentException("Ya existe un bloque de disponibilidad que se superpone en ese horario");
        }
    }

    private Disponibilidad buscarPropia(String emailProfesional, Long id) {
        Disponibilidad disponibilidad = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disponibilidad no encontrada"));

        if (!disponibilidad.getProfesional().getEmail().equals(emailProfesional)) {
            throw new AccessDeniedException("No tenés permiso para modificar esta disponibilidad");
        }

        return disponibilidad;
    }

    private Profesional buscarProfesional(String email) {
        return profesionalRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profesional no encontrado"));
    }
}