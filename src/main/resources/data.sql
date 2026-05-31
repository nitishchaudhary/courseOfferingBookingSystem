INSERT INTO users (id, username, email, role, time_zone)
VALUES
    ('a0000000-0000-0000-0000-000000000001', 'Nitish Chaudhary', 'nitish@gmail.com', 'TEACHER', 'Asia/Kolkata'),
    ('b0000000-0000-0000-0000-000000000002', 'Prashant Singh', 'prashant@gmail.com', 'PARENT', 'America/New_York')
    ON CONFLICT (email) DO NOTHING;