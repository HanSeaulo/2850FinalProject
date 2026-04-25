PRAGMA foreign_keys = ON;
 
-- ============================================================
-- SEED DATA FOR FLIGHT BOOKING DATABASE (SQLite / Exposed v1)
-- Generated for: 2026-04-25
-- ============================================================
 
-- USERS
INSERT INTO Users (name, email, role, password, created_at) VALUES
('Alice Johnson',  'alice.johnson@email.com',  'admin',    'hashed_pw_1',  '2024-01-05 09:00:00'),
('Bob Smith',      'bob.smith@email.com',       'customer', 'hashed_pw_2',  '2024-01-10 11:30:00'),
('Carol White',    'carol.white@email.com',     'customer', 'hashed_pw_3',  '2024-02-14 08:15:00'),
('David Brown',    'david.brown@email.com',     'customer', 'hashed_pw_4',  '2024-03-01 14:00:00'),
('Emma Davis',     'emma.davis@email.com',      'customer', 'hashed_pw_5',  '2024-03-20 10:45:00'),
('Frank Miller',   'frank.miller@email.com',    'agent',    'hashed_pw_6',  '2024-04-05 09:30:00'),
('Grace Wilson',   'grace.wilson@email.com',    'customer', 'hashed_pw_7',  '2024-04-18 16:00:00'),
('Henry Moore',    'henry.moore@email.com',     'customer', 'hashed_pw_8',  '2024-05-02 13:20:00'),
('Isla Taylor',    'isla.taylor@email.com',     'agent',    'hashed_pw_9',  '2024-05-15 09:00:00'),
('Jack Anderson',  'jack.anderson@email.com',   'customer', 'hashed_pw_10', '2024-06-01 10:00:00'),
('Karen Hughes',   'karen.hughes@email.com',    'customer', 'hashed_pw_11', '2026-04-25 08:05:00'),
('Leo Patel',      'leo.patel@email.com',       'customer', 'hashed_pw_12', '2026-04-25 09:30:00'),
('Mia Chen',       'mia.chen@email.com',        'customer', 'hashed_pw_13', '2026-04-25 11:00:00'),
('Noah Evans',     'noah.evans@email.com',      'customer', 'hashed_pw_14', '2026-04-25 13:45:00'),
('Olivia Scott',   'olivia.scott@email.com',    'admin',    'hashed_pw_15', '2026-04-25 14:20:00');
 
-- FLIGHTS
INSERT INTO Flights (flight_number, departure_airport, arrival_airport, departure_time, arrival_time, price, total_seats, available_seats, created_at) VALUES
('BA101', 'LHR', 'JFK', '2025-03-01 08:00:00', '2025-03-01 11:00:00', 450.00,  200, 0,   '2024-12-01 09:00:00'),
('AA202', 'JFK', 'LAX', '2025-03-02 10:30:00', '2025-03-02 13:45:00', 210.00,  180, 0,   '2024-12-01 09:05:00'),
('EK303', 'DXB', 'SYD', '2025-06-03 23:00:00', '2025-06-04 18:30:00', 890.00,  350, 50,  '2025-01-01 09:10:00'),
('LH404', 'FRA', 'SIN', '2025-09-05 14:00:00', '2025-09-06 08:00:00', 720.00,  280, 95,  '2025-03-01 09:15:00'),
('QF505', 'SYD', 'LAX', '2026-04-25 02:00:00', '2026-04-25 22:30:00', 1100.00, 300, 0,   '2026-01-01 09:20:00'),
('FR606', 'DUB', 'BCN', '2026-04-25 06:30:00', '2026-04-25 10:00:00', 89.00,   189, 0,   '2026-01-01 09:25:00'),
('UA707', 'ORD', 'MIA', '2026-04-25 13:00:00', '2026-04-25 17:30:00', 175.00,  160, 0,   '2026-01-01 09:30:00'),
('SQ808', 'SIN', 'NRT', '2026-04-25 10:00:00', '2026-04-25 17:30:00', 520.00,  250, 0,   '2026-01-01 09:35:00'),
('AF909', 'CDG', 'JFK', '2026-04-25 18:00:00', '2026-04-25 21:00:00', 600.00,  220, 80,  '2026-01-01 09:40:00'),
('TK010', 'IST', 'LHR', '2026-04-25 20:30:00', '2026-04-25 23:00:00', 310.00,  190, 120, '2026-01-01 09:45:00'),
('BA202', 'LHR', 'DXB', '2026-05-01 07:00:00', '2026-05-01 17:00:00', 380.00,  200, 145, '2026-02-01 09:00:00'),
('EK404', 'DXB', 'JFK', '2026-05-10 09:00:00', '2026-05-10 15:00:00', 750.00,  350, 200, '2026-02-01 09:05:00'),
('LH505', 'FRA', 'LHR', '2026-06-01 06:00:00', '2026-06-01 07:30:00', 120.00,  180, 175, '2026-02-01 09:10:00');
 
-- BOOKINGS
INSERT INTO Bookings (user_id, flight_id, status, total_price, created_at) VALUES
(2,  1,  'confirmed', 450.00,  '2025-01-10 10:00:00'),
(3,  1,  'confirmed', 900.00,  '2025-01-12 14:30:00'),
(4,  2,  'confirmed', 210.00,  '2025-01-15 09:00:00'),
(5,  3,  'confirmed', 890.00,  '2025-05-20 16:00:00'),
(7,  4,  'confirmed', 720.00,  '2025-08-22 11:00:00'),
(8,  5,  'confirmed', 1100.00, '2026-01-25 08:45:00'),
(2,  6,  'cancelled', 89.00,   '2026-01-28 13:00:00'),
(10, 7,  'confirmed', 175.00,  '2026-02-01 10:30:00'),
(3,  8,  'confirmed', 520.00,  '2026-02-05 09:15:00'),
(4,  9,  'confirmed', 600.00,  '2026-02-08 15:00:00'),
(5,  10, 'confirmed', 310.00,  '2026-02-10 12:00:00'),
(7,  11, 'confirmed', 380.00,  '2026-03-12 11:30:00'),
(8,  12, 'cancelled', 750.00,  '2026-03-14 08:00:00'),
(10, 13, 'confirmed', 120.00,  '2026-03-15 14:00:00'),
(6,  3,  'refunded',  890.00,  '2025-05-16 09:00:00'),
(11, 9,  'confirmed', 600.00,  '2026-04-25 08:10:00'),
(12, 10, 'confirmed', 310.00,  '2026-04-25 09:45:00'),
(13, 11, 'pending',   380.00,  '2026-04-25 10:30:00'),
(14, 12, 'confirmed', 750.00,  '2026-04-25 11:15:00'),
(15, 13, 'confirmed', 120.00,  '2026-04-25 12:00:00'),
(1,  9,  'pending',   600.00,  '2026-04-25 13:05:00'),
(2,  10, 'confirmed', 310.00,  '2026-04-25 14:30:00'),
(3,  12, 'confirmed', 750.00,  '2026-04-25 15:00:00');
 
-- PASSENGERS
INSERT INTO Passengers (booking_id, first_name, last_name, email) VALUES
(1,  'Bob',    'Smith',    'bob.smith.pax@email.com'),
(2,  'Carol',  'White',    'carol.white.pax@email.com'),
(3,  'Tom',    'White',    'tom.white.pax@email.com'),
(4,  'David',  'Brown',    'david.brown.pax@email.com'),
(5,  'Emma',   'Davis',    'emma.davis.pax@email.com'),
(5,  'James',  'Davis',    'james.davis.pax@email.com'),
(6,  'Grace',  'Wilson',   'grace.wilson.pax@email.com'),
(7,  'Henry',  'Moore',    'henry.moore.pax@email.com'),
(7,  'Sophie', 'Moore',    'sophie.moore.pax@email.com'),
(8,  'Jack',   'Anderson', 'jack.anderson.pax@email.com'),
(9,  'Carol',  'White',    'carol.white2.pax@email.com'),
(9,  'Tom',    'White',    'tom.white2.pax@email.com'),
(10, 'David',  'Brown',    'david.brown2.pax@email.com'),
(11, 'Emma',   'Davis',    'emma.davis2.pax@email.com'),
(12, 'Grace',  'Wilson',   'grace.wilson2.pax@email.com'),
(12, 'Mark',   'Wilson',   'mark.wilson.pax@email.com'),
(13, 'Henry',  'Moore',    'henry.moore2.pax@email.com'),
(14, 'Jack',   'Anderson', 'jack.anderson2.pax@email.com'),
(16, 'Karen',  'Hughes',   'karen.hughes.pax@email.com'),
(17, 'Leo',    'Patel',    'leo.patel.pax@email.com'),
(17, 'Sara',   'Patel',    'sara.patel.pax@email.com'),
(18, 'Mia',    'Chen',     'mia.chen.pax@email.com'),
(19, 'Noah',   'Evans',    'noah.evans.pax@email.com'),
(20, 'Olivia', 'Scott',    'olivia.scott.pax@email.com'),
(21, 'Alice',  'Johnson',  'alice.johnson.pax@email.com'),
(22, 'Bob',    'Smith',    'bob.smith2.pax@email.com'),
(23, 'Carol',  'White',    'carol.white3.pax@email.com'),
(23, 'Tom',    'White',    'tom.white3.pax@email.com');
 
-- REQUESTS
INSERT INTO Requests (booking_id, type, status, created_at, processed_at) VALUES
(7,  'cancellation', 'approved', '2026-01-29 09:00:00', '2026-01-29 11:30:00'),
(13, 'cancellation', 'approved', '2026-03-14 10:00:00', '2026-03-14 14:00:00'),
(15, 'refund',       'approved', '2025-05-17 08:00:00', '2025-05-18 09:00:00'),
(5,  'upgrade',      'approved', '2025-05-21 12:00:00', '2025-05-21 14:00:00'),
(8,  'meal_request', 'approved', '2026-02-02 10:00:00', '2026-02-02 10:30:00'),
(9,  'upgrade',      'rejected', '2026-02-06 11:00:00', '2026-02-06 15:00:00'),
(10, 'seat_change',  'approved', '2026-02-09 09:30:00', '2026-02-09 10:00:00'),
(1,  'meal_request', 'approved', '2025-01-11 08:00:00', '2025-01-11 08:15:00'),
(6,  'upgrade',      'rejected', '2026-01-29 10:00:00', '2026-01-29 11:00:00'),
(14, 'meal_request', 'approved', '2026-03-15 15:00:00', '2026-03-15 15:10:00'),
(16, 'meal_request', 'pending',  '2026-04-25 08:15:00', NULL),
(17, 'seat_change',  'pending',  '2026-04-25 09:50:00', NULL),
(18, 'upgrade',      'pending',  '2026-04-25 10:35:00', NULL),
(19, 'meal_request', 'approved', '2026-04-25 11:20:00', '2026-04-25 11:25:00'),
(21, 'seat_change',  'pending',  '2026-04-25 13:10:00', NULL),
(22, 'cancellation', 'pending',  '2026-04-25 14:35:00', NULL);
 
-- REPORTS
INSERT INTO Reports (generated_at, report_type) VALUES
('2025-01-31 23:59:00', 'monthly_bookings'),
('2025-01-31 23:59:01', 'monthly_revenue'),
('2025-02-28 23:59:00', 'monthly_bookings'),
('2025-02-28 23:59:01', 'monthly_revenue'),
('2025-02-28 23:59:02', 'cancellations'),
('2025-03-31 23:59:00', 'monthly_bookings'),
('2025-03-31 23:59:01', 'monthly_revenue'),
('2026-01-31 23:59:00', 'monthly_bookings'),
('2026-01-31 23:59:01', 'monthly_revenue'),
('2026-02-28 23:59:00', 'monthly_bookings'),
('2026-02-28 23:59:01', 'monthly_revenue'),
('2026-03-31 23:59:00', 'monthly_bookings'),
('2026-03-31 23:59:01', 'monthly_revenue'),
('2026-03-31 23:59:02', 'flight_occupancy'),
('2026-04-25 06:00:00', 'weekly_summary'),
('2026-04-25 12:00:00', 'flight_occupancy');