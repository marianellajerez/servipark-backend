-- V2__Add_activo_to_tipos_vehiculo.sql
ALTER TABLE tipos_vehiculo
ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE;

-- Asegura que los existentes queden activos
UPDATE tipos_vehiculo SET activo = TRUE WHERE activo IS NULL;