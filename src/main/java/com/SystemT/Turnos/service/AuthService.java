package com.SystemT.Turnos.service;

import com.SystemT.Turnos.Dto.auth.AuthRequest;
import com.SystemT.Turnos.Dto.auth.AuthResponse;
import com.SystemT.Turnos.Dto.auth.ProfesionalResponse;
import com.SystemT.Turnos.Dto.auth.RegisterRequest;
import com.SystemT.Turnos.Entity.Profesional;
import com.SystemT.Turnos.Mapper.ProfesionalMapper;
import com.SystemT.Turnos.Repository.ProfesionalRepository;
import com.SystemT.Turnos.Security.JwtService;
import com.SystemT.Turnos.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ProfesionalRepository profesionalRepository;
    private final ProfesionalMapper profesionalMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public ProfesionalResponse register(RegisterRequest request) {
        if (profesionalRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Ya existe un profesional registrado con ese email");
        }

        Profesional profesional = profesionalMapper.toEntity(request);
        profesional.setPassword(passwordEncoder.encode(request.password()));
        profesional.setSlug(generarSlugUnico(request.nombre()));

        Profesional guardado = profesionalRepository.save(profesional);
        return profesionalMapper.toResponse(guardado);
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        Profesional profesional = profesionalRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Profesional no encontrado"));

        String token = jwtService.generateToken(profesional);
        return new AuthResponse(token);
    }

    private String generarSlugUnico(String nombre) {
        String slugBase = SlugGenerator.generar(nombre);
        String slug = slugBase;
        int contador = 2;

        while (profesionalRepository.existsBySlug(slug)) {
            slug = slugBase + "-" + contador;
            contador++;
        }

        return slug;
    }
}