CREATE TABLE servicio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    public_id CHAR(36) NOT NULL UNIQUE,
    profesional_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    duracion_min INT NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT fk_servicio_profesional FOREIGN KEY (profesional_id) REFERENCES profesional(id)
);