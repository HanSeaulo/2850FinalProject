# COMP2850 Flight Booking System

## How to Run
1. Navigate to the /code directory (type "cd code")
2. Run `./gradlew run` to start the server
3. Access the site at 'http://localhost:8080'

## Project Overview
This is a web-based flight booking system built with **Kotlin** and **Ktor**. It handles user registration, flight searching, and a seat selection system. 

## Features
- **User Auth:** Sign up and log in.
- **Flight Search:** Find flights based on origin and destination.
- **Seat Booking:** Interactive seat selection with logic to prevent double-booking.
- **Stats Dashboard:** Admin view for total bookings, active flights, and user activity.

## Testing & CI/CD
We've implemented tests to make sure the backend is stable.
- **Unit Tests:** Covers database access logic, user auth, and helper functions.
- **Edge Cases:** Tests for invalid emails, blank passwords, and empty search results.
- **GitHub Actions:** We set up a CI pipeline that automatically runs all tests on every push to ensure the build is always green.

### Team members:
- Hanan Jahangiri
- Laman Shukurova
- Nyle Holdsworth
- Libby Atack
