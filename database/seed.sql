-- SLOTS Seed Data for Development/Testing
-- Run this AFTER schema.sql

-- Insert test user
-- IMPORTANT: Replace 'test-firebase-uid-here' with an actual Firebase UID from your
-- Firebase project (found in Firebase Console → Authentication → Users → UID column).
INSERT INTO users (id, email, name, firebase_uid)
VALUES 
    ('00000000-0000-0000-0000-000000000001', 'test@example.com', 'Test User', 'test-firebase-uid-here')
ON CONFLICT (email) DO NOTHING;

-- Insert sample tasks
INSERT INTO tasks (user_id, title, description, category, priority, deadline, status)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'Complete project report', 'Write Q4 summary report for team review', 'WORK', 'HIGH', NOW() + INTERVAL '3 days', 'PENDING'),
    ('00000000-0000-0000-0000-000000000001', 'Study for exam', 'Review chapters 5-8 for upcoming test', 'STUDY', 'HIGH', NOW() + INTERVAL '7 days', 'PENDING'),
    ('00000000-0000-0000-0000-000000000001', 'Morning jog', '30 minute run in the park', 'HEALTH', 'MEDIUM', NULL, 'PENDING'),
    ('00000000-0000-0000-0000-000000000001', 'Buy groceries', 'Weekly grocery shopping', 'PERSONAL', 'LOW', NOW() + INTERVAL '2 days', 'PENDING'),
    ('00000000-0000-0000-0000-000000000001', 'Read clean code book', 'Read chapters 1-3', 'STUDY', 'MEDIUM', NOW() + INTERVAL '14 days', 'COMPLETED')
ON CONFLICT DO NOTHING;

-- Insert sample transactions
INSERT INTO transactions (user_id, type, amount, category, description, date)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'INCOME', 3000.00, 'SALARY', 'Monthly salary', NOW() - INTERVAL '5 days'),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 150.00, 'FOOD', 'Weekly groceries', NOW() - INTERVAL '4 days'),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 50.00, 'TRANSPORT', 'Monthly bus pass', NOW() - INTERVAL '3 days'),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 200.00, 'BILLS', 'Electricity bill', NOW() - INTERVAL '2 days'),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 80.00, 'EDUCATION', 'Online course subscription', NOW() - INTERVAL '1 day'),
    ('00000000-0000-0000-0000-000000000001', 'INCOME', 500.00, 'OTHER', 'Freelance project payment', NOW())
ON CONFLICT DO NOTHING;

-- Insert sample debts
INSERT INTO debts (user_id, person_name, amount, type, description, status)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'John Smith', 100.00, 'LENT', 'Borrowed for lunch last week', 'PENDING'),
    ('00000000-0000-0000-0000-000000000001', 'Sarah Johnson', 250.00, 'BORROWED', 'Concert tickets split', 'PENDING'),
    ('00000000-0000-0000-0000-000000000001', 'Mike Davis', 50.00, 'LENT', 'Coffee and snacks', 'SETTLED')
ON CONFLICT DO NOTHING;

-- Insert sample budget limit
INSERT INTO budget_limits (user_id, monthly_limit, month)
VALUES
    ('00000000-0000-0000-0000-000000000001', 2000.00, TO_CHAR(NOW(), 'YYYY-MM'))
ON CONFLICT (user_id, month) DO NOTHING;
