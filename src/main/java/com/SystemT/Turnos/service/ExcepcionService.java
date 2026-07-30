package com.SystemT.Turnos.service;


import com.SystemT.Turnos.Dto.Exception.ExcepcionRequest;
import com.SystemT.Turnos.Dto.Exception.ExcepcionResponse;
import com.SystemT.Turnos.Entity.Excepcion;
import com.SystemT.Turnos.Entity.Profesional;
import com.SystemT.Turnos.Mapper.ExcepcionMapper;
import com.SystemT.Turnos.Repository.ExcepcionRepository;
import com.SystemT.Turnos.Repository.ProfesionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcepcionService {

    private final ExcepcionRepository excepcionRepository;
    private final ProfesionalRepository profesionalRepository;
    private final ExcepcionMapper excepcionMapper;

    public ExcepcionResponse crear(String emailProfesional, ExcepcionRequest request) {
        validarHorario(request);
        Profesional profesional = buscarProfesional(emailProfesional);

        Excepcion excepcion = excepcionMapper.toEntity(request);
        excepcion.setProfesional(profesional);

        Excepcion guardada = excepcionRepository.save(excepcion);
        return excepcionMapper.toResponse(guardada);
    }

    public List<ExcepcionResponse> listarPropias(String emailProfesional) {
        Profesional profesional = buscarProfesional(emailProfesional);
        return excepcionRepository.findByProfesionalId(profesional.getId())
                .stream()
                .map(excepcionMapper::toResponse)
                .toList();
    }

    public void eliminar(String emailProfesional, Long id) {
        Excepcion excepcion = buscarPropia(emailProfesional, id);
        excepcionRepository.delete(excepcion);
    }

    private void validarHorario(ExcepcionRequest request) {
        boolean tieneInicio = request.horaInicio() != null;
        boolean tieneFin = request.horaFin() != null;

        if (tieneInicio != tieneFin) {
            throw new IllegalArgumentException(
                    "Si especificás una hora de inicio o fin, ambas son obligatorias (o dejá las dos vacías para bloquear el día completo)"
            );
        }

        if (tieneInicio && !request.horaInicio().isBefore(request.horaFin())) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
        }
    }

    private Excepcion buscarPropia(String emailProfesional, Long id) {
        Excepcion excepcion = excepcionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Excepción no encontrada"));

        if (!excepcion.getProfesional().getEmail().equals(emailProfesional)) {
            throw new AccessDeniedException("No tenés permiso para modificar esta excepción");
        }

        return excepcion;
    }

    private Profesional buscarProfesional(String email) {
        return profesionalRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profesional no encontrado"));
    }
}