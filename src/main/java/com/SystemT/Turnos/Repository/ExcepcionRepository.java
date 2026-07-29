package com.SystemT.Turnos.Repository;

import com.SystemT.Turnos.Entity.Excepcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExcepcionRepository extends JpaRepository<Excepcion, Long> {

    List<Excepcion> findByProfesionalIdAndFecha(Long profesionalId, LocalDate fecha);

    List<Excepcion> findByProfesionalIdAndFechaBetween(Long profesionalId, LocalDate desde, LocalDate hasta);
}