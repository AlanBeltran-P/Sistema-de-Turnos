package com.SystemT.Turnos.Controller;

import com.SystemT.Turnos.Dto.auth.AuthRequest;
import com.SystemT.Turnos.Dto.auth.AuthResponse;
import com.SystemT.Turnos.Dto.auth.ProfesionalResponse;
import com.SystemT.Turnos.Dto.auth.RegisterRequest;
import com.SystemT.Turnos.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ProfesionalResponse> register(@Valid @RequestBody RegisterRequest request) {
        ProfesionalResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}