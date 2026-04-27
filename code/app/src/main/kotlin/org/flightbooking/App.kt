package org.flightbooking

import access.FlightAccess
import access.UserAccess
import database.DBFactory
import statsaccess.ManagementDashboard
import io.ktor.http.*
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
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import tables.*

@Serializable
data class FlightCardDto(
    val id: Int,
    val flightNumber: String,
    val from: String,
    val to: String,
    val departureTime: String,
    val arrivalTime: String,
    val price: Double,
    val availableSeats: Int
)

@Serializable
data class FlightPriceDto(val flightId: Int, val price: Double)

@Serializable
data class ManagementStatsDto(
    val todaysBookings: Int,
    val yesterdayBookings: Int,
    val activeFlights: Int,
    val registeredUsers: Int,
    val totalFlights: Int,
    val openRequests: Int
)

@Serializable
data class ActivityDto(
    val time: String,
    val action: String,
    val details: String,
    val status: String
)

fun main() {
    embeddedServer(Netty, port = 8080) {
        DBFactory.init()

        install(ContentNegotiation) {
            json()
        }

        data class UserSession(val name: String)

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
                    call.sessions.set(UserSession(name))
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
                    call.sessions.set(UserSession(user.name))
                    call.respondRedirect("/home.html")
                } else {
                    call.respondText("Wrong email or password")
                }
            }

            post("/payment") {
                val params = call.receiveParameters()
                val totalPrice = params["totalPrice"] ?: "0"
                val selectedSeats = params["selectedSeats"] ?: ""
                val flightId = params["flightId"] ?: ""

                call.respondText(
                    "Payment submitted successfully for flight $flightId. Seats: ${if (selectedSeats.isBlank()) "None selected" else selectedSeats}. Total: $$totalPrice",
                    ContentType.Text.Plain
                )
            }

            get("/current-user") {
                val session = call.sessions.get<UserSession>()
                call.respondText(session?.name ?: "")
            }

            get("/logout") {
                call.sessions.clear<UserSession>()
                call.respondRedirect("/home.html")
            }

            get("/api/flights") {
                val from = call.request.queryParameters["from"]
                val to = call.request.queryParameters["to"]
                val qty = call.request.queryParameters["qty"]?.toIntOrNull() ?: 1
                val depart = call.request.queryParameters["depart"]

                val flights = FlightAccess().getAll()
                    .asSequence()
                    .filter { from.isNullOrBlank() || it.departureAirport == from }
                    .filter { to.isNullOrBlank() || it.arrivalAirport == to }
                    .filter { it.availableSeats >= qty }
                    .filter {
                        if (depart.isNullOrBlank()) true
                        else it.departureTime.date.toString() >= depart
                    }
                    .sortedBy { it.departureTime }
                    .map {
                        FlightCardDto(
                            id = it.id,
                            flightNumber = it.flightNumber,
                            from = it.departureAirport,
                            to = it.arrivalAirport,
                            departureTime = it.departureTime.toString(),
                            arrivalTime = it.arrivalTime.toString(),
                            price = it.price,
                            availableSeats = it.availableSeats
                        )
                    }
                    .toList()

                call.respond(flights)
            }

            get("/flight-price") {
                val flightId = call.request.queryParameters["flightId"]?.toIntOrNull()
                val flight = flightId?.let { FlightAccess().getFlightById(it) }
                if (flight == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Flight not found"))
                } else {
                    call.respond(FlightPriceDto(flight.id, flight.price))
                }
            }

            get("/api/management-stats") {
    val dashboard = ManagementDashboard()

    val stats = transaction {
        ManagementStatsDto(
            todaysBookings = dashboard.TodaysBookings(),
            yesterdayBookings = dashboard.TodaysBookings(-1),
            activeFlights = dashboard.ActiveFlights(),
            registeredUsers = dashboard.RegisteredUsers(),
            totalFlights = FlightsTable.selectAll().count().toInt(),
            openRequests = RequestsTable.selectAll().count().toInt()
        )
    }

    call.respond(stats)
}

            get("/api/recent-activity") {
                val activity = transaction {
                    val bookingItems = BookingsTable.selectAll()
                        .orderBy(BookingsTable.createdAt, SortOrder.DESC)
                        .limit(5)
                        .map {
                            ActivityDto(
                                time = it[BookingsTable.createdAt].toString(),
                                action = "Booking created",
                                details = "Booking #${it[BookingsTable.id]} • flight ${it[BookingsTable.flightId]}",
                                status = it[BookingsTable.status].replaceFirstChar { c -> c.uppercase() }
                            )
                        }

                    val userItems = UsersTable.selectAll()
                        .orderBy(UsersTable.createdAt, SortOrder.DESC)
                        .limit(5)
                        .map {
                            ActivityDto(
                                time = it[UsersTable.createdAt].toString(),
                                action = "User registered",
                                details = it[UsersTable.email],
                                status = "OK"
                            )
                        }

                    val requestItems = RequestsTable.selectAll()
                        .orderBy(RequestsTable.createdAt, SortOrder.DESC)
                        .limit(5)
                        .map {
                            ActivityDto(
                                time = it[RequestsTable.createdAt].toString(),
                                action = "Request submitted",
                                details = "${it[RequestsTable.type]} for booking #${it[RequestsTable.bookingId]}",
                                status = it[RequestsTable.status].replaceFirstChar { c -> c.uppercase() }
                            )
                        }

                    (bookingItems + userItems + requestItems)
                        .sortedByDescending { it.time }
                        .take(10)
                }
                call.respond(activity)
            }

            staticResources("/", "static")
        }
    }.start(wait = true)
}