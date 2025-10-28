-- V1__Initial_Schema.sql
-- Creación de todas las tablas para ServiPark

-- Tabla de Roles
CREATE TABLE roles (
    id_rol BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla de Usuarios
CREATE TABLE usuarios (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    id_rol BIGINT NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    ultima_modificacion DATETIME(6),
    CONSTRAINT FK_USUARIO_ROL FOREIGN KEY (id_rol) REFERENCES roles (id_rol)
);

-- Tabla de Tipos de Vehículo
CREATE TABLE tipos_vehiculo (
    id_tipo_vehiculo BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    fecha_creacion DATETIME(6) NOT NULL,
    ultima_modificacion DATETIME(6)
);

-- Tabla de Vehículos
CREATE TABLE vehiculos (
    id_vehiculo BIGINT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(10) NOT NULL UNIQUE,
    id_tipo_vehiculo BIGINT NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    ultima_modificacion DATETIME(6),
    CONSTRAINT FK_VEHICULO_TIPO FOREIGN KEY (id_tipo_vehiculo) REFERENCES tipos_vehiculo (id_tipo_vehiculo)
);

-- Tabla de Tarifas
CREATE TABLE tarifas (
    id_tarifa BIGINT AUTO_INCREMENT PRIMARY KEY,
    valor_por_minuto DOUBLE NOT NULL,
    fecha_inicio DATETIME(6) NOT NULL,
    fecha_fin DATETIME(6),
    id_tipo_vehiculo BIGINT NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    ultima_modificacion DATETIME(6),
    CONSTRAINT FK_TARIFA_TIPO FOREIGN KEY (id_tipo_vehiculo) REFERENCES tipos_vehiculo (id_tipo_vehiculo)
);

-- Tabla de Tickets
CREATE TABLE tickets (
    id_ticket BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_ingreso DATETIME(6) NOT NULL,
    fecha_salida DATETIME(6),
    valor_total DOUBLE,
    id_usuario BIGINT NOT NULL,
    id_vehiculo BIGINT NOT NULL,
    id_tarifa BIGINT NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    ultima_modificacion DATETIME(6),
    CONSTRAINT FK_TICKET_USUARIO FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario),
    CONSTRAINT FK_TICKET_VEHICULO FOREIGN KEY (id_vehiculo) REFERENCES vehiculos (id_vehiculo),
    CONSTRAINT FK_TICKET_TARIFA FOREIGN KEY (id_tarifa) REFERENCES tarifas (id_tarifa)
);

-- Insertar los roles fijos
INSERT INTO roles (nombre) VALUES ('ADMINISTRADOR');
INSERT INTO roles (nombre) VALUES ('CAJERO');