package com.SystemT.Turnos.Security;

import com.SystemT.Turnos.Repository.ProfesionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ProfesionalRepository profesionalRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return profesionalRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profesional no encontrado"));
    }
}