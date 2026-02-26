SELECT * FROM users;

-- PRAGMA foreign_keys = ON;

-- CREATE TABLE users (
--   id INTEGER PRIMARY KEY AUTOINCREMENT,
--   name TEXT,
--   email TEXT UNIQUE,
--   role TEXT,
--   password TEXT,
--   created_at DATETIME DEFAULT CURRENT_TIMESTAMP
-- );

-- CREATE TABLE flights (
--   id INTEGER PRIMARY KEY AUTOINCREMENT,
--   flight_number TEXT,
--   departure_airport TEXT,
--   arrival_airport TEXT,
--   departure_time DATETIME,
--   arrival_time DATETIME,
--   price REAL,
--   total_seats INTEGER,
--   available_seats INTEGER,
--   created_at DATETIME DEFAULT CURRENT_TIMESTAMP
-- );

-- CREATE TABLE bookings (
--   id INTEGER PRIMARY KEY AUTOINCREMENT,
--   user_id INTEGER,
--   flight_id INTEGER,
--   status TEXT,
--   total_price REAL,
--   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
--   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
--   FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE
-- );

-- CREATE TABLE passengers (
--   id INTEGER PRIMARY KEY AUTOINCREMENT,
--   booking_id INTEGER,
--   first_name TEXT,
--   last_name TEXT,
--   email TEXT,
--   FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
-- );

-- CREATE TABLE requests (
--   id INTEGER PRIMARY KEY AUTOINCREMENT,
--   booking_id INTEGER,
--   type TEXT,
--   status TEXT,
--   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
--   processed_at DATETIME,
--   FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
-- );

-- CREATE TABLE reports (
--   id INTEGER PRIMARY KEY AUTOINCREMENT,
--   generated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
--   report_type TEXT
-- );