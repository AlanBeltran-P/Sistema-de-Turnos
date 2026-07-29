CREATE TABLE turno (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    public_id CHAR(36) NOT NULL UNIQUE,
    profesional_id BIGINT NOT NULL,
    servicio_id BIGINT NOT NULL,
    cliente_nombre VARCHAR(150) NOT NULL,
    cliente_telefono VARCHAR(30) NOT NULL,
    cliente_email VARCHAR(150),
    fecha_hora_inicio DATETIME NOT NULL,
    fecha_hora_fin DATETIME NOT NULL,
    estado VARCHAR(20) NOT NULL,
    cancelacion_token CHAR(36) NOT NULL UNIQUE,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_turno_profesional FOREIGN KEY (profesional_id) REFERENCES profesional(id),
    CONSTRAINT fk_turno_servicio FOREIGN KEY (servicio_id) REFERENCES servicio(id),
    CONSTRAINT uq_turno_profesional_horario UNIQUE (profesional_id, fecha_hora_inicio)
);