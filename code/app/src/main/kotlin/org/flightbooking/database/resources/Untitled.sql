CREATE TABLE `users` (
  `id` integer PRIMARY KEY,
  `name` varchar(255),
  `email` varchar(255) UNIQUE,
  `role` varchar(255),
  `password` varchar(255),
  `created_at` timestamp
);

CREATE TABLE `flights` (
  `id` integer PRIMARY KEY,
  `flight_number` varchar(255),
  `departure_airport` varchar(255),
  `arrival_airport` varchar(255),
  `departure_time` timestamp,
  `arrival_time` timestamp,
  `price` decimal,
  `total_seats` integer,
  `available_seats` integer,
  `created_at` timestamp
);

CREATE TABLE `bookings` (
  `id` integer PRIMARY KEY,
  `user_id` integer,
  `flight_id` integer,
  `status` varchar(255),
  `total_price` decimal,
  `created_at` timestamp
);

CREATE TABLE `passengers` (
  `id` integer PRIMARY KEY,
  `booking_id` integer,
  `first_name` varchar(255),
  `last_name` varchar(255),
  `email` varchar(255)
);

CREATE TABLE `requests` (
  `id` integer PRIMARY KEY,
  `booking_id` integer,
  `type` varchar(255),
  `status` varchar(255),
  `created_at` timestamp,
  `processed_at` timestamp
);

CREATE TABLE `reports` (
  `id` integer PRIMARY KEY,
  `generated_at` timestamp,
  `report_type` varchar(255)
);

ALTER TABLE `bookings` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `bookings` ADD FOREIGN KEY (`flight_id`) REFERENCES `flights` (`id`);

ALTER TABLE `passengers` ADD FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`);

ALTER TABLE `requests` ADD FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`);
