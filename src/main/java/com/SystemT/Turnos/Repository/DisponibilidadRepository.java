package com.SystemT.Turnos.Repository;

import com.SystemT.Turnos.Entity.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad,Long> {

    List<Disponibilidad> findByProfesionalId(Long profesionalId);

    List<Disponibilidad> findByProfesionalIdAndDiaSemana(Long profesionalId, Integer diaSemana);

    void deleteByProfesionalIdAndDiaSemana(Long profesionalId, Integer diaSemana);

}
