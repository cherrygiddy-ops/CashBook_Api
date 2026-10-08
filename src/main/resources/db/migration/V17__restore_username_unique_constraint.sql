ALTER TABLE cashbook_user
    ADD CONSTRAINT uk_cashbook_user_username UNIQUE (username);