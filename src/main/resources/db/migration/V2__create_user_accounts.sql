-- V2__create_user_accounts.sql

-- Habilita generación de UUID (si no está disponible, instala uuid-ossp)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tabla de usuarios autenticados por Auth0
CREATE TABLE IF NOT EXISTS user_accounts (
                                             id          uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    auth0_sub   text NOT NULL UNIQUE,         -- sub de Auth0 (p.ej. "auth0|abc123")
    email       text NOT NULL,
    name        text,
    picture     text,
    created_at  timestamptz NOT NULL DEFAULT now(),
    updated_at  timestamptz NOT NULL DEFAULT now()
    );

-- Índices útiles
CREATE INDEX IF NOT EXISTS idx_user_accounts_email ON user_accounts (email);

-- (Opcional) FK desde permissions.user_id -> user_accounts.id
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'public' AND table_name = 'permissions'
  ) THEN
BEGIN
ALTER TABLE permissions
    ADD CONSTRAINT fk_permissions_user
        FOREIGN KEY (user_id) REFERENCES user_accounts(id) ON DELETE CASCADE;
EXCEPTION
      WHEN duplicate_object THEN NULL;
END;
END IF;
END$$;
