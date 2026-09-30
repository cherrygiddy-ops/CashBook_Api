-- V3__update_cashbook_user.sql



-- Add username
ALTER TABLE cashbook_user
    ADD COLUMN username VARCHAR(255);

-- Populate username for existing records
UPDATE cashbook_user
SET username = split_part(email, '@', 1)
WHERE username IS NULL;

-- Make username required
ALTER TABLE cashbook_user
    ALTER COLUMN username SET NOT NULL;

-- Username unique
ALTER TABLE cashbook_user
    ADD CONSTRAINT uk_cashbook_user_username UNIQUE (username);


-- Account verification status
ALTER TABLE cashbook_user
    ADD COLUMN verified BOOLEAN NOT NULL DEFAULT TRUE;


-- Account lock status
ALTER TABLE cashbook_user
    ADD COLUMN locked BOOLEAN NOT NULL DEFAULT FALSE;


-- Last login
ALTER TABLE cashbook_user
    ADD COLUMN last_login TIMESTAMP;


-- Profile picture
ALTER TABLE cashbook_user
    ADD COLUMN profile_picture_url VARCHAR(255);


-- Password reset
ALTER TABLE cashbook_user
    ADD COLUMN reset_token VARCHAR(255);

ALTER TABLE cashbook_user
    ADD COLUMN reset_token_expiry TIMESTAMP;


-- Email verification
ALTER TABLE cashbook_user
    ADD COLUMN verification_token VARCHAR(255);

ALTER TABLE cashbook_user
    ADD COLUMN verification_token_expiry TIMESTAMP;


-- Token uniqueness
ALTER TABLE cashbook_user
    ADD CONSTRAINT uk_cashbook_user_reset_token UNIQUE (reset_token);

ALTER TABLE cashbook_user
    ADD CONSTRAINT uk_cashbook_user_verification_token UNIQUE (verification_token);