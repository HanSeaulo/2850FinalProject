package org.flightbooking

import access.SeatAccess
import access.FlightAccess
import access.UserAccess
import database.DBFactory
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.formUrlEncode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.pebble.Pebble
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.pebbletemplates.pebble.loader.ClasspathLoader
import java.io.File
import java.sql.DriverManager

fun dbUrl(): String {
    val dbFile = File("src/main/kotlin/org/flightbooking/database/resources/Database.db")
    return "jdbc:sqlite:${dbFile.absolutePath}"
}

fun sqlText(value: String?): String? = value?.takeIf { it.isNotBlank() }

fun esc(value: String?): String =
    (value ?: "")
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")

fun main() {
    embeddedServer(Netty, port = 8080) {
        DBFactory.init()

        install(ContentNegotiation) {
            json()
        }

        data class UserSession(val name: String, val email: String)

        install(Sessions) {
            cookie<UserSession>("user_session")
        }

        install(Pebble) {
            loader(ClasspathLoader().apply { prefix = "templates" })
        }

        routing {
            get("/") { call.respondRedirect("/home.html") }
            get("/login") { call.respondRedirect("/login.html") }
            get("/signup") { call.respondRedirect("/signup.html") }
            get("/payment") { call.respondRedirect("/payment.html") }
            get("/report") { call.respondRedirect("/report.html") }
            get("/management") { call.respondRedirect("/management.html") }

            get("/search") {
                val airports = FlightAccess().getAirportCodes()
                call.respond(io.ktor.server.pebble.PebbleContent("search.peb", mapOf("airports" to airports)))
            }

            get("/flights") {
                val query = call.request.queryParameters.formUrlEncode()
                val suffix = if (query.isNotBlank()) "?$query" else ""
                call.respondRedirect("/flights.html$suffix")
            }

            post("/signup") {
                val params = call.receiveParameters()
                val name = params["name"] ?: ""
                val email = params["email"] ?: ""
                val password = params["password"] ?: ""

                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                    call.respondRedirect("/signup.html")
                    return@post
                }

                val success = UserAccess().createUser(name, email, password, "user")
                if (success) {
                    call.sessions.set(UserSession(name, email))
                    call.respondRedirect("/home.html")
                } else {
                    call.respondText("Email already exists")
                }
            }

            post("/login") {
                val params = call.receiveParameters()
                val email = params["email"] ?: ""
                val password = params["password"] ?: ""

                if (email.isBlank() || password.isBlank()) {
                    call.respondRedirect("/login.html")
                    return@post
                }

                val user = UserAccess().getUserByEmail(email)
                if (user != null && user.password == password) {
                    call.sessions.set(UserSession(user.name, user.email))
                    call.respondRedirect("/home.html")
                } else {
                    call.respondText("Wrong email or password")
                }
            }
            post("/book-seat") {
                val params = call.receiveParameters()
                val flightId = params["flightId"]?.toIntOrNull()
                val seatNumber = params["seatNumber"] ?: ""

                if (flightId == null || seatNumber.isBlank()) {
                    call.respondText("Missing seat info", status = HttpStatusCode.BadRequest)
                    return@post
                }

                val success = SeatAccess().bookSeat(flightId, seatNumber)

                if (success) {
                    call.respondText("OK")
                } else {
                    call.respondText("Seat already booked", status = HttpStatusCode.Conflict)
                }
            }

            post("/payment") {
                val session = call.sessions.get<UserSession>()
                if (session == null) {
                    call.respondRedirect("/login.html")
                    return@post
                }

                val params = call.receiveParameters()
                val totalPrice = params["totalPrice"]?.toDoubleOrNull() ?: 0.0
                val selectedSeats = (params["selectedSeats"] ?: "")
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                val flightId = params["flightId"]?.toIntOrNull()

                if (flightId == null || selectedSeats.isEmpty()) {
                    call.respondText("Missing flight or seats", status = HttpStatusCode.BadRequest)
                    return@post
                }

                var bookingId: Int? = null

                try {
                    DriverManager.getConnection(dbUrl()).use { conn ->
                        conn.autoCommit = false

                        try {
                            val userId = conn.prepareStatement(
                                "SELECT id FROM Users WHERE email = ? LIMIT 1"
                            ).use { stmt ->
                                stmt.setString(1, session.email)
                                stmt.executeQuery().use { rs ->
                                    if (rs.next()) rs.getInt("id") else null
                                }
                            }

                            if (userId == null) {
                                conn.rollback()
                                call.respondText("User not found", status = HttpStatusCode.Unauthorized)
                                return@post
                            }

                            val availability = conn.prepareStatement(
                                "SELECT available_seats FROM Flights WHERE id = ? LIMIT 1"
                            ).use { stmt ->
                                stmt.setInt(1, flightId)
                                stmt.executeQuery().use { rs ->
                                    if (rs.next()) rs.getInt("available_seats") else null
                                }
                            }

                            if (availability == null) {
                                conn.rollback()
                                call.respondText("Flight not found", status = HttpStatusCode.NotFound)
                                return@post
                            }

                            if (availability < selectedSeats.size) {
                                conn.rollback()
                                call.respondText("Not enough seats remaining", status = HttpStatusCode.BadRequest)
                                return@post
                            }

                            bookingId = conn.prepareStatement(
                                """
                                INSERT INTO Bookings (user_id, flight_id, status, total_price, created_at)
                                VALUES (?, ?, 'confirmed', ?, datetime('now'))
                                """.trimIndent(),
                                java.sql.Statement.RETURN_GENERATED_KEYS
                            ).use { stmt ->
                                stmt.setInt(1, userId)
                                stmt.setInt(2, flightId)
                                stmt.setDouble(3, totalPrice)
                                stmt.executeUpdate()

                                stmt.generatedKeys.use { keys ->
                                    if (keys.next()) keys.getInt(1) else null
                                }
                            }

                            if (bookingId == null) {
                                conn.rollback()
                                call.respondText("Could not create booking", status = HttpStatusCode.InternalServerError)
                                return@post
                            }

                            val parts = session.name.trim().split(Regex("\\s+"))
                            val firstName = parts.firstOrNull() ?: "Guest"
                            val lastName = if (parts.size > 1) parts.drop(1).joinToString(" ") else "Passenger"

                            conn.prepareStatement(
                                "INSERT INTO Passengers (booking_id, first_name, last_name, email) VALUES (?, ?, ?, ?)"
                            ).use { stmt ->
                                selectedSeats.forEachIndexed { index, _ ->
                                    stmt.setInt(1, bookingId!!)
                                    stmt.setString(2, if (index == 0) firstName else "$firstName ${index + 1}")
                                    stmt.setString(3, lastName)
                                    stmt.setString(
                                        4,
                                        if (index == 0) session.email else session.email.replace("@", "+${index + 1}@")
                                    )
                                    stmt.addBatch()
                                }
                                stmt.executeBatch()
                            }

                            conn.prepareStatement(
                                "UPDATE Flights SET available_seats = available_seats - ? WHERE id = ?"
                            ).use { stmt ->
                                stmt.setInt(1, selectedSeats.size)
                                stmt.setInt(2, flightId)
                                stmt.executeUpdate()
                            }

                            conn.commit()
                        } catch (e: Exception) {
                            conn.rollback()
                            throw e
                        }
                    }

                    val next = listOf(
                        "bookingId=${bookingId}",
                        "flightId=$flightId",
                        "seats=${selectedSeats.joinToString(",")}",
                        "total=${"%.2f".format(totalPrice)}"
                    ).joinToString("&")

                    call.respondRedirect("/confirmation.html?$next")
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respondText(
                        "Payment route error: ${e::class.qualifiedName}: ${e.message}",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }

            get("/current-user") {
                val session = call.sessions.get<UserSession>()
                call.respondText(session?.name ?: "")
            }

            get("/logout") {
                call.sessions.clear<UserSession>()
                call.respondRedirect("/home.html")
            }
            get("/booked-seats") {
                val flightId = call.request.queryParameters["flightId"]?.toIntOrNull()

                if (flightId == null) {
                    call.respondText("[]", ContentType.Application.Json)
                    return@get
                }

                val seats = SeatAccess().getBookedSeats(flightId)
                val json = seats.joinToString(prefix = "[", postfix = "]") { seat ->
                    "\"$seat\""
                }

                call.respondText(json, ContentType.Application.Json)
            }

            get("/api/flights") {
                try {
                    val from = sqlText(call.request.queryParameters["from"])
                    val to = sqlText(call.request.queryParameters["to"])
                    val qty = call.request.queryParameters["qty"]?.toIntOrNull() ?: 1
                    val depart = sqlText(call.request.queryParameters["depart"])

                    val rows = mutableListOf<String>()

                    DriverManager.getConnection(dbUrl()).use { conn ->
                        val sql = """
                            SELECT id, flight_number, departure_airport, arrival_airport,
                                   departure_time, arrival_time, price, available_seats
                            FROM Flights
                            WHERE (? IS NULL OR departure_airport = ?)
                              AND (? IS NULL OR arrival_airport = ?)
                              AND available_seats >= ?
                              AND (? IS NULL OR substr(departure_time, 1, 10) >= ?)
                            ORDER BY departure_time
                        """.trimIndent()

                        conn.prepareStatement(sql).use { stmt ->
                            stmt.setString(1, from)
                            stmt.setString(2, from)
                            stmt.setString(3, to)
                            stmt.setString(4, to)
                            stmt.setInt(5, qty)
                            stmt.setString(6, depart)
                            stmt.setString(7, depart)

                            stmt.executeQuery().use { rs ->
                                while (rs.next()) {
                                    rows.add(
                                        """
                                        {
                                          "id": ${rs.getInt("id")},
                                          "flightNumber": "${esc(rs.getString("flight_number"))}",
                                          "from": "${esc(rs.getString("departure_airport"))}",
                                          "to": "${esc(rs.getString("arrival_airport"))}",
                                          "departureTime": "${esc(rs.getString("departure_time"))}",
                                          "arrivalTime": "${esc(rs.getString("arrival_time"))}",
                                          "price": ${rs.getDouble("price")},
                                          "availableSeats": ${rs.getInt("available_seats")}
                                        }
                                        """.trimIndent()
                                    )
                                }
                            }
                        }
                    }

                    call.respondText("[${rows.joinToString(",")}]", ContentType.Application.Json)
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respondText(
                        "Flights route error: ${e.message}",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }

            get("/flight-price") {
                try {
                    val flightId = call.request.queryParameters["flightId"]?.toIntOrNull()
                    if (flightId == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing flightId"))
                        return@get
                    }

                    DriverManager.getConnection(dbUrl()).use { conn ->
                        conn.prepareStatement("SELECT id, price FROM Flights WHERE id = ?").use { stmt ->
                            stmt.setInt(1, flightId)
                            stmt.executeQuery().use { rs ->
                                if (rs.next()) {
                                    call.respondText(
                                        """{"flightId":${rs.getInt("id")},"price":${rs.getDouble("price")}}""",
                                        ContentType.Application.Json
                                    )
                                } else {
                                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Flight not found"))
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respondText(
                        "Flight price route error: ${e.message}",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }

            get("/api/management-stats") {
                try {
                    DriverManager.getConnection(dbUrl()).use { conn ->
                        fun count(sql: String): Int =
                            conn.createStatement().use { stmt ->
                                stmt.executeQuery(sql).use { rs ->
                                    if (rs.next()) rs.getInt(1) else 0
                                }
                            }

                        val json = """
                            {
                              "todaysBookings": ${count("SELECT COUNT(*) FROM Bookings WHERE date(created_at) = date('now')")},
                              "yesterdayBookings": ${count("SELECT COUNT(*) FROM Bookings WHERE date(created_at) = date('now', '-1 day')")},
                              "activeFlights": ${count("SELECT COUNT(*) FROM Flights WHERE departure_time <= datetime('now') AND arrival_time >= datetime('now')")},
                              "registeredUsers": ${count("SELECT COUNT(*) FROM Users")},
                              "totalFlights": ${count("SELECT COUNT(*) FROM Flights")},
                              "openRequests": ${count("SELECT COUNT(*) FROM Requests WHERE lower(status) <> 'approved'")}
                            }
                        """.trimIndent()

                        call.respondText(json, ContentType.Application.Json)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respondText(
                        "Management route error: ${e.message}",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }

            get("/api/recent-activity") {
                try {
                    val rows = mutableListOf<String>()

                    DriverManager.getConnection(dbUrl()).use { conn ->
                        val sql = """
                            SELECT created_at AS time, 'Booking created' AS action,
                                   'Booking #' || id || ' • flight ' || flight_id AS details,
                                   status
                            FROM Bookings
                            UNION ALL
                            SELECT created_at AS time, 'User registered' AS action,
                                   email AS details,
                                   'OK' AS status
                            FROM Users
                            UNION ALL
                            SELECT created_at AS time, 'Request submitted' AS action,
                                   type || ' for booking #' || booking_id AS details,
                                   status
                            FROM Requests
                            ORDER BY time DESC
                            LIMIT 10
                        """.trimIndent()

                        conn.createStatement().use { stmt ->
                            stmt.executeQuery(sql).use { rs ->
                                while (rs.next()) {
                                    rows.add(
                                        """
                                        {
                                          "time": "${esc(rs.getString("time"))}",
                                          "action": "${esc(rs.getString("action"))}",
                                          "details": "${esc(rs.getString("details"))}",
                                          "status": "${esc(rs.getString("status"))}"
                                        }
                                        """.trimIndent()
                                    )
                                }
                            }
                        }
                    }

                    call.respondText("[${rows.joinToString(",")}]", ContentType.Application.Json)
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respondText(
                        "Recent activity route error: ${e.message}",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }

            get("/api/my-bookings") {
    val session = call.sessions.get<UserSession>()
    if (session == null) {
        call.respond(HttpStatusCode.Unauthorized, "You must be logged in")
        return@get
    }

    try {
        val rows = mutableListOf<String>()

        DriverManager.getConnection(dbUrl()).use { conn ->
            val sql = """
                SELECT 
                    b.id,
                    b.flight_id,
                    b.status,
                    b.created_at,
                    COALESCE(b.total_price, 0) AS total_price,
                    f.flight_number,
                    f.departure_airport,
                    f.arrival_airport,
                    f.departure_time,
                    f.arrival_time,
                    COUNT(p.id) AS passengers
                FROM Bookings b
                JOIN Users u ON b.user_id = u.id
                JOIN Flights f ON b.flight_id = f.id
                LEFT JOIN Passengers p ON p.booking_id = b.id
                WHERE u.email = ?
                GROUP BY 
                    b.id,
                    b.flight_id,
                    b.status,
                    b.created_at,
                    b.total_price,
                    f.flight_number,
                    f.departure_airport,
                    f.arrival_airport,
                    f.departure_time,
                    f.arrival_time
                ORDER BY b.created_at DESC
            """.trimIndent()

            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, session.email)

                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        rows.add(
                            """
                            {
                              "id": "${esc(rs.getString("id"))}",
                              "flightId": "${esc(rs.getString("flight_id"))}",
                              "status": "${esc(rs.getString("status"))}",
                              "createdAt": "${esc(rs.getString("created_at"))}",
                              "totalPrice": ${rs.getDouble("total_price")},
                              "flightNumber": "${esc(rs.getString("flight_number"))}",
                              "from": "${esc(rs.getString("departure_airport"))}",
                              "to": "${esc(rs.getString("arrival_airport"))}",
                              "departureTime": "${esc(rs.getString("departure_time"))}",
                              "arrivalTime": "${esc(rs.getString("arrival_time"))}",
                              "passengers": ${rs.getInt("passengers")}
                            }
                            """.trimIndent()
                        )
                    }
                }
            }
        }

        call.respondText("[${rows.joinToString(",")}]", ContentType.Application.Json)
    } catch (e: Exception) {
        e.printStackTrace()
        call.respondText(
            "My bookings route error: ${e.message}",
            status = HttpStatusCode.InternalServerError
        )
    }
}
            staticResources("/", "static")
        }
    }.start(wait = true)
}