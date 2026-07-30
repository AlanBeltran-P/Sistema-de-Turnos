package com.SystemT.Turnos.Repository;

import com.SystemT.Turnos.Entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServicioRepository extends JpaRepository<Servicio,Long> {
    List<Servicio> findByProfesionalIdAndActivoTrue(Long profesionalId);

    Optional<Servicio> findByPublicId(UUID publicId);

    List<Servicio> findByProfesional_SlugAndActivoTrue(String slug);

    Optional<Servicio> findByPublicIdAndProfesional_SlugAndActivoTrue(UUID publicId, String slug);

}
