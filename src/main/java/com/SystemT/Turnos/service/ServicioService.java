package com.SystemT.Turnos.service;


import com.SystemT.Turnos.Dto.servicio.ServicioRequest;
import com.SystemT.Turnos.Dto.servicio.ServicioResponse;
import com.SystemT.Turnos.Entity.Profesional;
import com.SystemT.Turnos.Entity.Servicio;
import com.SystemT.Turnos.Mapper.ServicioMapper;
import com.SystemT.Turnos.Repository.ProfesionalRepository;
import com.SystemT.Turnos.Repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServicioService {

    private final ServicioRepository servicioRepository;
    private final ProfesionalRepository profesionalRepository;
    private final ServicioMapper servicioMapper;

    public ServicioResponse crear(String emailProfesional, ServicioRequest request) {
        Profesional profesional = buscarProfesional(emailProfesional);

        Servicio servicio = servicioMapper.toEntity(request);
        servicio.setProfesional(profesional);
        servicio.setActivo(true);

        Servicio guardado = servicioRepository.save(servicio);
        return servicioMapper.toResponse(guardado);
    }

    public List<ServicioResponse> listarPropios(String emailProfesional) {
        Profesional profesional = buscarProfesional(emailProfesional);
        return servicioRepository.findByProfesionalIdAndActivoTrue(profesional.getId())
                .stream()
                .map(servicioMapper::toResponse)
                .toList();
    }

    public ServicioResponse actualizar(String emailProfesional, UUID publicId, ServicioRequest request) {
        Servicio servicio = buscarPropio(emailProfesional, publicId);
        servicioMapper.actualizarDesdeRequest(request, servicio);
        return servicioMapper.toResponse(servicioRepository.save(servicio));
    }

    public void desactivar(String emailProfesional, UUID publicId) {
        Servicio servicio = buscarPropio(emailProfesional, publicId);
        servicio.setActivo(false);
        servicioRepository.save(servicio);
    }

    private Servicio buscarPropio(String emailProfesional, UUID publicId) {
        Servicio servicio = servicioRepository.findByPublicId(publicId)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));

        if (!servicio.getProfesional().getEmail().equals(emailProfesional)) {
            throw new AccessDeniedException("No tenés permiso para modificar este servicio");
        }

        return servicio;
    }

    private Profesional buscarProfesional(String email) {
        return profesionalRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profesional no encontrado"));
    }

    public List<ServicioResponse> listarPublicos(String slug) {
        return servicioRepository.findByProfesional_SlugAndActivoTrue(slug)
                .stream()
                .map(servicioMapper::toResponse)
                .toList();
    }
}