CREATE TABLE disponibilidad (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profesional_id BIGINT NOT NULL,
    dia_semana INT NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    CONSTRAINT fk_disponibilidad_profesional FOREIGN KEY (profesional_id) REFERENCES profesional(id)
);