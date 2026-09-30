-- V6__align_schema_with_entities.sql

-- =========================================================
-- BUSINESSES
-- =========================================================

-- Copy the old business name into the new column where needed
UPDATE businesses
SET business_name = name
WHERE business_name IS NULL
   OR business_name = '';

-- Add columns required by Business.java
ALTER TABLE businesses
    ADD COLUMN email VARCHAR(255);

ALTER TABLE businesses
    ADD COLUMN phone_number VARCHAR(30);


-- =========================================================
-- CONTACTS
-- =========================================================

-- Contact entity uses phoneNumber -> phone_number
ALTER TABLE contacts
    RENAME COLUMN phone TO phone_number;


-- =========================================================
-- TRANSACTIONS
-- =========================================================

-- Transaction entity field names
ALTER TABLE transactions
    RENAME COLUMN transaction_type TO type;

ALTER TABLE transactions
    RENAME COLUMN description TO remarks;

ALTER TABLE transactions
    RENAME COLUMN reference_number TO transaction_number;

-- Field exists in Transaction.java but not in the original table
ALTER TABLE transactions
    ADD COLUMN running_balance NUMERIC(19,2);