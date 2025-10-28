-- V3__Add_activo_to_usuarios.sql
ALTER TABLE usuarios
ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE;

-- Asegura que los existentes queden activos
UPDATE usuarios SET activo = TRUE WHERE activo IS NULL;