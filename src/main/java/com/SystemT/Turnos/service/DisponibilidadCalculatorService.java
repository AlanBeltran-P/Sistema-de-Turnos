package com.SystemT.Turnos.service;


import com.SystemT.Turnos.Dto.disponibilidad.SlotDisponibleResponse;
import com.SystemT.Turnos.Entity.Excepcion;
import com.SystemT.Turnos.Entity.Servicio;
import com.SystemT.Turnos.Entity.Turno;
import com.SystemT.Turnos.Repository.DisponibilidadRepository;
import com.SystemT.Turnos.Repository.ExcepcionRepository;
import com.SystemT.Turnos.Repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DisponibilidadCalculatorService {

    private final DisponibilidadRepository disponibilidadRepository;
    private final ExcepcionRepository excepcionRepository;
    private final TurnoRepository turnoRepository;

    private record Rango(LocalTime inicio, LocalTime fin) {}

    public List<SlotDisponibleResponse> calcularSlots(Long profesionalId, Servicio servicio, LocalDate fecha) {
        int diaSemana = convertirDiaSemana(fecha.getDayOfWeek());

        List<Rango> bloquesBase = disponibilidadRepository
                .findByProfesionalIdAndDiaSemana(profesionalId, diaSemana)
                .stream()
                .map(d -> new Rango(d.getHoraInicio(), d.getHoraFin()))
                .toList();

        List<Excepcion> excepciones = excepcionRepository.findByProfesionalIdAndFecha(profesionalId, fecha);

        List<Rango> bloquesLibres = restarExcepciones(bloquesBase, excepciones);

        List<Rango> slotsCandidatos = generarSlots(bloquesLibres, servicio.getDuracionMin());

        LocalDateTime inicioDelDia = fecha.atStartOfDay();
        LocalDateTime finDelDia = fecha.atTime(LocalTime.MAX);
        List<Turno> turnosOcupados = turnoRepository.findOcupadosEnRango(profesionalId, inicioDelDia, finDelDia);

        return slotsCandidatos.stream()
                .filter(slot -> !seSuperponeConAlgunTurno(slot, fecha, turnosOcupados))
                .map(r -> new SlotDisponibleResponse(r.inicio(), r.fin()))
                .toList();
    }

    private int convertirDiaSemana(DayOfWeek dayOfWeek) {
        // java.time.DayOfWeek: MONDAY=1 ... SUNDAY=7
        // Nuestra convención: DOMINGO=0 ... SABADO=6
        return dayOfWeek == DayOfWeek.SUNDAY ? 0 : dayOfWeek.getValue();
    }

    private List<Rango> restarExcepciones(List<Rango> bloques, List<Excepcion> excepciones) {
        List<Rango> resultado = new ArrayList<>(bloques);

        for (Excepcion excepcion : excepciones) {
            if (excepcion.getHoraInicio() == null) {
                // Bloqueo de día completo: no queda nada libre
                return List.of();
            }

            List<Rango> conRecorte = new ArrayList<>();
            for (Rango bloque : resultado) {
                conRecorte.addAll(recortar(bloque, excepcion.getHoraInicio(), excepcion.getHoraFin()));
            }
            resultado = conRecorte;
        }

        return resultado;
    }

    // Resta el intervalo [desde, hasta) de un rango, puede partirlo en 0, 1 o 2 pedazos
    private List<Rango> recortar(Rango rango, LocalTime desde, LocalTime hasta) {
        boolean seSuperponen = desde.isBefore(rango.fin()) && rango.inicio().isBefore(hasta);
        if (!seSuperponen) {
            return List.of(rango);
        }

        List<Rango> pedazos = new ArrayList<>();
        if (rango.inicio().isBefore(desde)) {
            pedazos.add(new Rango(rango.inicio(), desde));
        }
        if (hasta.isBefore(rango.fin())) {
            pedazos.add(new Rango(hasta, rango.fin()));
        }
        return pedazos;
    }

    private List<Rango> generarSlots(List<Rango> bloquesLibres, int duracionMin) {
        List<Rango> slots = new ArrayList<>();

        for (Rango bloque : bloquesLibres) {
            LocalTime cursor = bloque.inicio();
            while (!cursor.plusMinutes(duracionMin).isAfter(bloque.fin())) {
                LocalTime finSlot = cursor.plusMinutes(duracionMin);
                slots.add(new Rango(cursor, finSlot));
                cursor = finSlot;
            }
        }

        return slots;
    }

    private boolean seSuperponeConAlgunTurno(Rango slot, LocalDate fecha, List<Turno> turnosOcupados) {
        LocalDateTime slotInicio = fecha.atTime(slot.inicio());
        LocalDateTime slotFin = fecha.atTime(slot.fin());

        return turnosOcupados.stream().anyMatch(turno ->
                slotInicio.isBefore(turno.getFechaHoraFin()) && turno.getFechaHoraInicio().isBefore(slotFin)
        );
    }
}