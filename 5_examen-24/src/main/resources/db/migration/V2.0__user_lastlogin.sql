-- TODO: Implement schema change to persist user lastlogin timestamp
ALTER TABLE users
    ADD COLUMN last_login TIMESTAMP NULL;
