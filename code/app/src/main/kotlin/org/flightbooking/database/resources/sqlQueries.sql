PRAGMA foreign_keys = ON;

-- ============================================================
-- SEED DATA FOR FLIGHT BOOKING DATABASE (SQLite)
-- ============================================================

-- USERS
INSERT INTO users (name, email, role, password, created_at) VALUES
('Alice Johnson',  'alice.johnson@email.com',  'admin',    'hashed_pw_1',  '2024-01-05 09:00:00'),
('Bob Smith',      'bob.smith@email.com',       'customer', 'hashed_pw_2',  '2024-01-10 11:30:00'),
('Carol White',    'carol.white@email.com',     'customer', 'hashed_pw_3',  '2024-02-14 08:15:00'),
('David Brown',    'david.brown@email.com',     'customer', 'hashed_pw_4',  '2024-03-01 14:00:00'),
('Emma Davis',     'emma.davis@email.com',      'customer', 'hashed_pw_5',  '2024-03-20 10:45:00'),
('Frank Miller',   'frank.miller@email.com',    'agent',    'hashed_pw_6',  '2024-04-05 09:30:00'),
('Grace Wilson',   'grace.wilson@email.com',    'customer', 'hashed_pw_7',  '2024-04-18 16:00:00'),
('Henry Moore',    'henry.moore@email.com',     'customer', 'hashed_pw_8',  '2024-05-02 13:20:00'),
('Isla Taylor',    'isla.taylor@email.com',     'agent',    'hashed_pw_9',  '2024-05-15 09:00:00'),
('Jack Anderson',  'jack.anderson@email.com',   'customer', 'hashed_pw_10', '2024-06-01 10:00:00');

-- FLIGHTS
INSERT INTO flights (flight_number, departure_airport, arrival_airport, departure_time, arrival_time, price, total_seats, available_seats, created_at) VALUES
('BA101', 'LHR', 'JFK', '2025-03-01 08:00:00', '2025-03-01 11:00:00', 450.00,  200, 45,  '2024-12-01 09:00:00'),
('AA202', 'JFK', 'LAX', '2025-03-02 10:30:00', '2025-03-02 13:45:00', 210.00,  180, 120, '2024-12-01 09:05:00'),
('EK303', 'DXB', 'SYD', '2025-03-03 23:00:00', '2025-03-04 18:30:00', 890.00,  350, 200, '2024-12-01 09:10:00'),
('LH404', 'FRA', 'SIN', '2025-03-05 14:00:00', '2025-03-06 08:00:00', 720.00,  280, 95,  '2024-12-01 09:15:00'),
('QF505', 'SYD', 'LAX', '2025-03-07 09:00:00', '2025-03-07 05:30:00', 1100.00, 300, 10,  '2024-12-01 09:20:00'),
('FR606', 'DUB', 'BCN', '2025-03-10 06:30:00', '2025-03-10 10:00:00', 89.00,   189, 75,  '2024-12-01 09:25:00'),
('UA707', 'ORD', 'MIA', '2025-03-12 15:00:00', '2025-03-12 19:30:00', 175.00,  160, 60,  '2024-12-01 09:30:00'),
('SQ808', 'SIN', 'NRT', '2025-03-15 07:00:00', '2025-03-15 14:30:00', 520.00,  250, 180, '2024-12-01 09:35:00'),
('AF909', 'CDG', 'JFK', '2025-03-18 11:00:00', '2025-03-18 14:00:00', 600.00,  220, 30,  '2024-12-01 09:40:00'),
('TK010', 'IST', 'LHR', '2025-03-20 05:30:00', '2025-03-20 08:00:00', 310.00,  190, 140, '2024-12-01 09:45:00');

-- BOOKINGS
INSERT INTO bookings (user_id, flight_id, status, total_price, created_at) VALUES
(2,  1,  'confirmed', 450.00,  '2025-01-10 10:00:00'),
(3,  1,  'confirmed', 900.00,  '2025-01-12 14:30:00'),
(4,  2,  'confirmed', 210.00,  '2025-01-15 09:00:00'),
(5,  3,  'confirmed', 890.00,  '2025-01-20 16:00:00'),
(7,  4,  'pending',   720.00,  '2025-01-22 11:00:00'),
(8,  5,  'confirmed', 2200.00, '2025-01-25 08:45:00'),
(2,  6,  'cancelled', 89.00,   '2025-01-28 13:00:00'),
(10, 7,  'confirmed', 350.00,  '2025-02-01 10:30:00'),
(3,  8,  'confirmed', 1040.00, '2025-02-05 09:15:00'),
(4,  9,  'pending',   600.00,  '2025-02-08 15:00:00'),
(5,  10, 'confirmed', 310.00,  '2025-02-10 12:00:00'),
(7,  2,  'confirmed', 420.00,  '2025-02-12 11:30:00'),
(8,  6,  'cancelled', 178.00,  '2025-02-14 08:00:00'),
(10, 1,  'confirmed', 450.00,  '2025-02-15 14:00:00'),
(6,  3,  'refunded',  890.00,  '2025-02-16 09:00:00');

-- PASSENGERS
INSERT INTO passengers (booking_id, first_name, last_name, email) VALUES
(1,  'Bob',    'Smith',    'bob.smith@email.com'),
(2,  'Carol',  'White',    'carol.white@email.com'),
(2,  'Tom',    'White',    'tom.white@email.com'),
(3,  'David',  'Brown',    'david.brown@email.com'),
(4,  'Emma',   'Davis',    'emma.davis@email.com'),
(5,  'Grace',  'Wilson',   'grace.wilson@email.com'),
(6,  'Henry',  'Moore',    'henry.moore@email.com'),
(6,  'Sophie', 'Moore',    'sophie.moore@email.com'),
(7,  'Bob',    'Smith',    'bob.smith@email.com'),
(8,  'Jack',   'Anderson', 'jack.anderson@email.com'),
(8,  'Lily',   'Anderson', 'lily.anderson@email.com'),
(9,  'Carol',  'White',    'carol.white@email.com'),
(9,  'Tom',    'White',    'tom.white@email.com'),
(10, 'David',  'Brown',    'david.brown@email.com'),
(11, 'Emma',   'Davis',    'emma.davis@email.com'),
(12, 'Grace',  'Wilson',   'grace.wilson@email.com'),
(12, 'Mark',   'Wilson',   'mark.wilson@email.com'),
(13, 'Henry',  'Moore',    'henry.moore@email.com'),
(13, 'Sophie', 'Moore',    'sophie.moore@email.com'),
(14, 'Jack',   'Anderson', 'jack.anderson@email.com');

-- REQUESTS
INSERT INTO requests (booking_id, type, status, created_at, processed_at) VALUES
(7,  'cancellation', 'approved', '2025-01-29 09:00:00', '2025-01-29 11:30:00'),
(13, 'cancellation', 'approved', '2025-02-14 10:00:00', '2025-02-14 14:00:00'),
(15, 'refund',       'approved', '2025-02-17 08:00:00', '2025-02-18 09:00:00'),
(5,  'seat_change',  'pending',  '2025-01-23 12:00:00', NULL),
(8,  'meal_request', 'approved', '2025-02-02 10:00:00', '2025-02-02 10:30:00'),
(9,  'upgrade',      'rejected', '2025-02-06 11:00:00', '2025-02-06 15:00:00'),
(10, 'seat_change',  'pending',  '2025-02-09 09:30:00', NULL),
(1,  'meal_request', 'approved', '2025-01-11 08:00:00', '2025-01-11 08:15:00'),
(6,  'upgrade',      'approved', '2025-01-26 10:00:00', '2025-01-27 09:00:00'),
(14, 'meal_request', 'pending',  '2025-02-15 15:00:00', NULL);

-- REPORTS
INSERT INTO reports (generated_at, report_type) VALUES
('2025-01-31 23:59:00', 'monthly_bookings'),
('2025-01-31 23:59:01', 'monthly_revenue'),
('2025-01-31 23:59:02', 'flight_occupancy'),
('2025-02-28 23:59:00', 'monthly_bookings'),
('2025-02-28 23:59:01', 'monthly_revenue'),
('2025-02-28 23:59:02', 'flight_occupancy'),
('2025-02-28 23:59:03', 'cancellations'),
('2025-01-01 06:00:00', 'weekly_summary'),
('2025-01-08 06:00:00', 'weekly_summary'),
('2025-01-15 06:00:00', 'weekly_summary');