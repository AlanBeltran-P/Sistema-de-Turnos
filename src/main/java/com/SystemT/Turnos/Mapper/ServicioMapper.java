package com.SystemT.Turnos.Mapper;

import com.SystemT.Turnos.Dto.servicio.ServicioRequest;
import com.SystemT.Turnos.Dto.servicio.ServicioResponse;
import com.SystemT.Turnos.Entity.Servicio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ServicioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "profesional", ignore = true)
    @Mapping(target = "activo", ignore = true)
    Servicio toEntity(ServicioRequest request);

    ServicioResponse toResponse(Servicio servicio);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "profesional", ignore = true)
    @Mapping(target = "activo", ignore = true)
    void actualizarDesdeRequest(ServicioRequest request, @MappingTarget Servicio servicio);
}