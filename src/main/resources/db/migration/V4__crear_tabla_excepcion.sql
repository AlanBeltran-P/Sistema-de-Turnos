CREATE TABLE excepcion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profesional_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NULL,
    hora_fin TIME NULL,
    motivo VARCHAR(150),
    CONSTRAINT fk_excepcion_profesional FOREIGN KEY (profesional_id) REFERENCES profesional(id)
);