-- ==========================================
-- ROLES
-- ==========================================

CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,

                       name VARCHAR(50) NOT NULL UNIQUE,

                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- CASHBOOK USER
-- ==========================================

CREATE TABLE cashbook_user (
                               id BIGSERIAL PRIMARY KEY,

                               first_name VARCHAR(150) NOT NULL,
                               last_name VARCHAR(150) NOT NULL,

                               email VARCHAR(255) NOT NULL UNIQUE,
                               phone_number VARCHAR(30) UNIQUE,

                               password VARCHAR(255) NOT NULL,

                               active BOOLEAN NOT NULL DEFAULT TRUE,

                               role_id BIGINT NOT NULL,

                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_user_role
                                   FOREIGN KEY (role_id)
                                       REFERENCES roles(id)
);

-- ==========================================
-- BUSINESSES
-- ==========================================

CREATE TABLE businesses (
                            id BIGSERIAL PRIMARY KEY,

                            name VARCHAR(200) NOT NULL,
                            description TEXT,

                            owner_id BIGINT NOT NULL,

                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_business_owner
                                FOREIGN KEY (owner_id)
                                    REFERENCES cashbook_user(id)
);

-- ==========================================
-- BUSINESS MEMBERS
-- ==========================================

CREATE TABLE business_members (
                                  id BIGSERIAL PRIMARY KEY,

                                  business_id BIGINT NOT NULL,
                                  user_id BIGINT NOT NULL,

                                  member_role VARCHAR(50) NOT NULL,

                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_member_business
                                      FOREIGN KEY (business_id)
                                          REFERENCES businesses(id),

                                  CONSTRAINT fk_member_user
                                      FOREIGN KEY (user_id)
                                          REFERENCES cashbook_user(id),

                                  CONSTRAINT uk_business_member
                                      UNIQUE (business_id, user_id)
);

-- ==========================================
-- CASHBOOKS
-- ==========================================

CREATE TABLE cashbooks (
                           id BIGSERIAL PRIMARY KEY,

                           name VARCHAR(150) NOT NULL,
                           description TEXT,

                           business_id BIGINT NOT NULL,

                           opening_balance NUMERIC(19,2) DEFAULT 0,

                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT fk_cashbook_business
                               FOREIGN KEY (business_id)
                                   REFERENCES businesses(id)
);

-- ==========================================
-- CATEGORIES
-- ==========================================

CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,

                            name VARCHAR(100) NOT NULL,
                            description TEXT,

                            business_id BIGINT NOT NULL,

                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_category_business
                                FOREIGN KEY (business_id)
                                    REFERENCES businesses(id)
);

-- ==========================================
-- CONTACTS
-- ==========================================

CREATE TABLE contacts (
                          id BIGSERIAL PRIMARY KEY,

                          name VARCHAR(150) NOT NULL,
                          phone VARCHAR(50),
                          email VARCHAR(255),

                          business_id BIGINT NOT NULL,

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_contact_business
                              FOREIGN KEY (business_id)
                                  REFERENCES businesses(id)
);

-- ==========================================
-- TRANSACTIONS
-- ==========================================

CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,

                              transaction_date DATE NOT NULL,

                              amount NUMERIC(19,2) NOT NULL,

                              transaction_type VARCHAR(50) NOT NULL,

                              payment_mode VARCHAR(50) NOT NULL,

                              description TEXT,

                              reference_number VARCHAR(100),

                              cashbook_id BIGINT NOT NULL,
                              category_id BIGINT,
                              contact_id BIGINT,

                              created_by BIGINT NOT NULL,

                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT fk_transaction_cashbook
                                  FOREIGN KEY (cashbook_id)
                                      REFERENCES cashbooks(id),

                              CONSTRAINT fk_transaction_category
                                  FOREIGN KEY (category_id)
                                      REFERENCES categories(id),

                              CONSTRAINT fk_transaction_contact
                                  FOREIGN KEY (contact_id)
                                      REFERENCES contacts(id),

                              CONSTRAINT fk_transaction_user
                                  FOREIGN KEY (created_by)
                                      REFERENCES cashbook_user(id)
);

-- ==========================================
-- AUDIT LOGS
-- ==========================================

CREATE TABLE audit_logs (
                            id BIGSERIAL PRIMARY KEY,

                            user_id BIGINT,

                            action VARCHAR(255) NOT NULL,

                            entity_name VARCHAR(100),

                            entity_id BIGINT,

                            details TEXT,

                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_audit_user
                                FOREIGN KEY (user_id)
                                    REFERENCES cashbook_user(id)
);

-- ==========================================
-- DEFAULT ROLES
-- ==========================================

INSERT INTO roles (name)
VALUES
    ('SUPER_ADMIN'),
    ('OWNER'),
    ('ACCOUNTANT'),
    ('CASHIER');
