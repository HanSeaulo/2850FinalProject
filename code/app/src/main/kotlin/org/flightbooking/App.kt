package org.flightbooking

import access.SeatAccess
import access.FlightAccess
import access.UserAccess
import access.BookingAccess
import access.StatsAccess
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
import kotlinx.datetime.LocalDateTime
import io.ktor.server.pebble.PebbleContent

data class UserSession(val name: String, val email: String)

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

        install(Sessions) {
            cookie<UserSession>("user_session") {
                cookie.maxAgeInSeconds = 60 * 60 * 24 // 24 hours
                cookie.path = "/"
            }
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
                    ?: run { call.respondRedirect("/login.html"); return@post }

                val params = call.receiveParameters()
                val totalPrice = params["totalPrice"]?.toDoubleOrNull() ?: 0.0
                val selectedSeats = (params["selectedSeats"] ?: "")
                    .split(",").map { it.trim() }.filter { it.isNotBlank() }
                val flightId = params["flightId"]?.toIntOrNull()
                    ?: run { call.respondText("Missing flight or seats", status = HttpStatusCode.BadRequest); return@post }

                if (selectedSeats.isEmpty()) {
                    call.respondText("Missing flight or seats", status = HttpStatusCode.BadRequest)
                    return@post
                }

                val result = BookingAccess().createBooking(session, flightId, selectedSeats, totalPrice)

                when (result) {
                    "USER_NOT_FOUND" -> call.respondText("User not found", status = HttpStatusCode.Unauthorized)
                    "FLIGHT_NOT_FOUND" -> call.respondText("Flight not found", status = HttpStatusCode.NotFound)
                    "NOT_ENOUGH_SEATS" -> call.respondText("Not enough seats remaining", status = HttpStatusCode.BadRequest)
                    "BOOKING_FAILED" -> call.respondText("Could not create booking", status = HttpStatusCode.InternalServerError)
                    else -> call.respondRedirect("/confirmation.html?bookingId=$result"+"&flightId=$flightId"+"&seats=${selectedSeats.joinToString(",")}"+"&total=$totalPrice")
                }
            }

            get("/current-user") {
                val session = call.sessions.get<UserSession>()
                call.respondText(session?.name ?: "")
            }

            post("/logout") {
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
                val from = call.request.queryParameters["from"] ?: ""
                val to = call.request.queryParameters["to"] ?: ""
                val depart = call.request.queryParameters["date"] ?: ""
                val qty = call.request.queryParameters["qty"]?.toIntOrNull() ?: 1
                val cabinRaw = call.request.queryParameters["class"] ?: "econ"

                val cabinClass = if (cabinRaw == "bus") "Business" else "Economy"

                if (from.isBlank() || to.isBlank() || depart.isBlank()) {
                    call.respondText("Missing search parameters", status = HttpStatusCode.BadRequest)
                    return@get
                }
                val departTime = LocalDateTime.parse("${depart}T00:00:00")
                val flights = FlightAccess().searchFlights(from, to, departTime, qty, cabinClass) ?: emptyList()

                call.respond(flights)
            }

            get("/flight-price") {
                val flightId = call.request.queryParameters["flightId"]?.toIntOrNull()
                    ?: run {
                        call.respondText("""{"error":"Missing flightId"}""", ContentType.Application.Json, HttpStatusCode.BadRequest)
                        return@get
                    }

                val result = FlightAccess().getFlightPrice(flightId)
                    ?: run {
                        call.respondText("""{"error":"Flight not found"}""", ContentType.Application.Json, HttpStatusCode.NotFound)
                        return@get
                    }

                call.respondText(
                    """{"flightId":${result.first},"price":${result.second}}""",
                    ContentType.Application.Json
                )
            }
            

            get("/api/management-stats") {
                val dashboard = StatsAccess()
                val json = """
                    {
                    "todaysBookings": ${dashboard.TodaysBookings()},
                    "yesterdayBookings": ${dashboard.TodaysBookings(-1)},
                    "activeFlights": ${dashboard.ActiveFlights()},
                    "registeredUsers": ${dashboard.RegisteredUsers()},
                    "totalFlights": ${dashboard.TotalFlights()},
                    "openRequests": ${dashboard.OpenRequests()}
                    }
                """.trimIndent()
                call.respondText(json, ContentType.Application.Json)
            } 

            get("/api/recent-activity") {
                val rows = StatsAccess().RecentActivity()

                val json = "[${rows.joinToString(",") { row ->
                    """{"time":"${row["time"]}","action":"${row["action"]}","details":"${row["details"]}","status":"${row["status"]}"}"""
                }}]"

                call.respondText(json, ContentType.Application.Json)
            }            


            get("/api/my-bookings") {
                val session = call.sessions.get<UserSession>()
                if (session == null) {
                    call.respond(HttpStatusCode.Unauthorized, "You must be logged in")
                    return@get
                }

                val rows = BookingAccess().MyBookings(session.email)

                val json = "[${rows.joinToString(",") { row ->
                    """
                    {
                    "id":            "${row["id"]}",
                    "flightId":      "${row["flightId"]}",
                    "status":        "${row["status"]}",
                    "createdAt":     "${row["createdAt"]}",
                    "totalPrice":    ${row["totalPrice"]},
                    "flightNumber":  "${row["flightNumber"]}",
                    "from":          "${row["from"]}",
                    "to":            "${row["to"]}",
                    "departureTime": "${row["departureTime"]}",
                    "arrivalTime":   "${row["arrivalTime"]}",
                    "passengers":    ${row["passengers"]}
                    }
                    """.trimIndent()
                }}]"

                call.respondText(json, ContentType.Application.Json)
            }
            staticResources("/", "static")
        }
    }.start(wait = true)
}