-- V4__change_user_role_to_enum.sql

-- Remove foreign key
ALTER TABLE cashbook_user
DROP CONSTRAINT IF EXISTS fk_user_role;

-- Remove role_id
ALTER TABLE cashbook_user
DROP COLUMN IF EXISTS role_id;

-- Add role as string
ALTER TABLE cashbook_user
    ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'USER';