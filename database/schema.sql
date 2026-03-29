-- SLOTS Database Schema for Supabase (PostgreSQL)
-- Run this in your Supabase SQL editor

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    firebase_uid VARCHAR(255) UNIQUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Tasks table
CREATE TABLE IF NOT EXISTS tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(500) NOT NULL,
    description TEXT DEFAULT '',
    category VARCHAR(50) NOT NULL CHECK (category IN ('STUDY', 'WORK', 'PERSONAL', 'HEALTH')),
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    deadline TIMESTAMPTZ,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED')),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(10) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    amount DECIMAL(12,2) NOT NULL CHECK (amount > 0),
    category VARCHAR(50) NOT NULL CHECK (category IN ('FOOD', 'TRANSPORT', 'BILLS', 'EDUCATION', 'SALARY', 'OTHER')),
    description TEXT DEFAULT '',
    date TIMESTAMPTZ DEFAULT NOW(),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Debts table
CREATE TABLE IF NOT EXISTS debts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    person_name VARCHAR(255) NOT NULL,
    amount DECIMAL(12,2) NOT NULL CHECK (amount > 0),
    type VARCHAR(10) NOT NULL CHECK (type IN ('BORROWED', 'LENT')),
    description TEXT DEFAULT '',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SETTLED')),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Budget limits table
CREATE TABLE IF NOT EXISTS budget_limits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    monthly_limit DECIMAL(12,2) NOT NULL CHECK (monthly_limit > 0),
    month VARCHAR(7) NOT NULL,  -- Format: YYYY-MM
    created_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(user_id, month)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks(user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
CREATE INDEX IF NOT EXISTS idx_tasks_category ON tasks(category);
CREATE INDEX IF NOT EXISTS idx_tasks_deadline ON tasks(deadline);
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(date);
CREATE INDEX IF NOT EXISTS idx_transactions_type ON transactions(type);
CREATE INDEX IF NOT EXISTS idx_debts_user_id ON debts(user_id);
CREATE INDEX IF NOT EXISTS idx_debts_status ON debts(status);

-- Row Level Security (RLS) policies
ALTER TABLE tasks ENABLE ROW LEVEL SECURITY;
ALTER TABLE transactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE debts ENABLE ROW LEVEL SECURITY;
ALTER TABLE budget_limits ENABLE ROW LEVEL SECURITY;
ALTER TABLE users ENABLE ROW LEVEL SECURITY;

-- RLS Policies: users can only access their own data
CREATE POLICY tasks_user_policy ON tasks
    USING (user_id IN (SELECT id FROM users WHERE firebase_uid = auth.uid()::text));

CREATE POLICY transactions_user_policy ON transactions
    USING (user_id IN (SELECT id FROM users WHERE firebase_uid = auth.uid()::text));

CREATE POLICY debts_user_policy ON debts
    USING (user_id IN (SELECT id FROM users WHERE firebase_uid = auth.uid()::text));

CREATE POLICY budget_limits_user_policy ON budget_limits
    USING (user_id IN (SELECT id FROM users WHERE firebase_uid = auth.uid()::text));

-- Updated_at trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply update trigger to relevant tables
CREATE TRIGGER update_tasks_updated_at
    BEFORE UPDATE ON tasks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_debts_updated_at
    BEFORE UPDATE ON debts
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
