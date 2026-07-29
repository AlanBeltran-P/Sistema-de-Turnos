package com.SystemT.Turnos.Repository;

import com.SystemT.Turnos.Entity.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfesionalRepository extends JpaRepository <Profesional,Long> {
    Optional<Profesional> findByEmail(String email);

    Optional<Profesional> findBySlug(String slug);

    Optional<Profesional> findByPublicId(UUID publicId);

    boolean existsByEmail(String email);

    boolean existsBySlug(String slug);
}
