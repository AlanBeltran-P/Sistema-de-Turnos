package com.SystemT.Turnos.Mapper;


import com.SystemT.Turnos.Dto.Exception.ExcepcionRequest;
import com.SystemT.Turnos.Dto.Exception.ExcepcionResponse;
import com.SystemT.Turnos.Entity.Excepcion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExcepcionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profesional", ignore = true)
    Excepcion toEntity(ExcepcionRequest request);

    ExcepcionResponse toResponse(Excepcion excepcion);
}
