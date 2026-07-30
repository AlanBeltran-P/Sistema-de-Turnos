package com.SystemT.Turnos.Mapper;


import com.SystemT.Turnos.Dto.disponibilidad.DisponibilidadRequest;
import com.SystemT.Turnos.Dto.disponibilidad.DisponibilidadResponse;
import com.SystemT.Turnos.Entity.Disponibilidad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DisponibilidadMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profesional", ignore = true)
    Disponibilidad toEntity(DisponibilidadRequest request);

    DisponibilidadResponse toResponse(Disponibilidad disponibilidad);
}
