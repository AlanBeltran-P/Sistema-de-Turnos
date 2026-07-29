package com.SystemT.Turnos.Mapper;

import com.SystemT.Turnos.Dto.auth.ProfesionalResponse;
import com.SystemT.Turnos.Dto.auth.RegisterRequest;
import com.SystemT.Turnos.Entity.Profesional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfesionalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    Profesional toEntity(RegisterRequest request);

    ProfesionalResponse toResponse(Profesional profesional);
}
