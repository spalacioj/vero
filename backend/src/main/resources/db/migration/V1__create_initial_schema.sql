CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE RESTRICT,
    name VARCHAR(100) NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    initial_balance NUMERIC(19, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT accounts_type_check
        CHECK (account_type IN ('CASH', 'CHECKING', 'SAVINGS', 'CREDIT_CARD')),
    CONSTRAINT accounts_name_per_user_unique UNIQUE (user_id, name)
);

CREATE TABLE categories (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE RESTRICT,
    name VARCHAR(100) NOT NULL,
    category_type VARCHAR(10) NOT NULL,
    color VARCHAR(7),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT categories_type_check CHECK (category_type IN ('INCOME', 'EXPENSE')),
    CONSTRAINT categories_color_check CHECK (color IS NULL OR color ~ '^#[0-9A-Fa-f]{6}$'),
    CONSTRAINT categories_name_per_user_unique UNIQUE (user_id, name, category_type)
);

CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES accounts (id) ON DELETE RESTRICT,
    category_id UUID REFERENCES categories (id) ON DELETE RESTRICT,
    entry_type VARCHAR(20) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    description VARCHAR(500),
    occurred_on DATE NOT NULL,
    transfer_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT transactions_entry_type_check
        CHECK (entry_type IN ('INCOME', 'EXPENSE', 'TRANSFER_IN', 'TRANSFER_OUT')),
    CONSTRAINT transactions_amount_positive_check CHECK (amount > 0),
    CONSTRAINT transactions_category_or_transfer_check CHECK (
        (entry_type IN ('INCOME', 'EXPENSE') AND category_id IS NOT NULL AND transfer_id IS NULL)
        OR
        (entry_type IN ('TRANSFER_IN', 'TRANSFER_OUT') AND category_id IS NULL AND transfer_id IS NOT NULL)
    )
);

CREATE INDEX accounts_user_id_idx ON accounts (user_id);
CREATE INDEX categories_user_id_idx ON categories (user_id);
CREATE INDEX transactions_account_occurred_on_idx ON transactions (account_id, occurred_on DESC);
CREATE INDEX transactions_category_occurred_on_idx ON transactions (category_id, occurred_on DESC);
CREATE INDEX transactions_transfer_id_idx ON transactions (transfer_id);
