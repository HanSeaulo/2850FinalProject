package org.flightbooking
import io.ktor.server.pebble.*
import io.ktor.http.*
import io.ktor.server.engine.embeddedServer
import kotlinx.datetime.LocalDateTime
import io.ktor.server.netty.Netty
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.http.content.*
import database.DBFactory
import access.*
import io.ktor.server.request.receiveParameters

fun FlightsTest() {
    println("Establishing DB Connection...")
    DBFactory.init()
    println("Connection successful")

    val flightAccess = FlightAccess()
    val getall = flightAccess.searchFlights("LHR", "JFK")

    println("All data in flights table:")
    println(getall.joinToString())
}

fun main() {
    embeddedServer(Netty, port = 8080) {
        DBFactory.init()

        install(ContentNegotiation) {
            json()
        }
        install(Pebble)
        routing {
            staticResources("/", "static")

            get("/") {
                call.respondRedirect("/home.html")
            }

            get("/login") {
                call.respondRedirect("/login.html")
            }

            get("/signup") {
                call.respondRedirect("/signup.html")
            }

            get("/search") {
                val airports = FlightAccess().getAirportCodes()
                call.respond(PebbleContent("search.peb", mapOf("airports" to airports)))
            }

            get("/payment") {
                call.respondRedirect("/payment.html")
            }

            get("/report") {
                call.respondRedirect("/report.html")
            }

            get("/management") {
                call.respondRedirect("/management.html")
            }

            get("/api/flights") {
                val flights = FlightAccess().getAll()
                val json = flights.joinToString(prefix = "[", postfix = "]") { f ->
                    """{"id":${f.id},"flightNumber":"${f.flightNumber}","from":"${f.departureAirport}","to":"${f.arrivalAirport}","price":${f.price}}"""
                }
                call.respondText(json, ContentType.Application.Json)
            }

            get("/flight-price") {
                val flightId = call.request.queryParameters["flightId"]?.toIntOrNull()

                if (flightId == null) {
                    call.respondText(
                        """{"error":"Missing or invalid flightId"}""",
                        ContentType.Application.Json,
                        HttpStatusCode.BadRequest
                    )
                    return@get
                }

                val flight = FlightAccess().getFlightById(flightId)

                if (flight == null) {
                    call.respondText(
                        """{"error":"Flight not found"}""",
                        ContentType.Application.Json,
                        HttpStatusCode.NotFound
                    )
                } else {
                    call.respondText(
                        """{"price":${flight.price},"flightNumber":"${flight.flightNumber}"}""",
                        ContentType.Application.Json
                    )
                }
            }

            get("/flights") {
                val from = call.request.queryParameters["from"] ?: ""
                val to = call.request.queryParameters["to"] ?: ""
                val depart = call.request.queryParameters["depart"] ?: ""
                val qty = call.request.queryParameters["qty"]?.toIntOrNull() ?: 1
                val cabinRaw = call.request.queryParameters["class"] ?: "econ"

                val cabinClass = when (cabinRaw) {
                    "bus" -> "Business"
                    else -> "Economy"
                }

                if (from.isBlank() || to.isBlank() || depart.isBlank()) {
                    call.respondText("Missing search parameters", status = HttpStatusCode.BadRequest)
                    return@get
                }

                val departTime = LocalDateTime.parse("${depart}T00:00:00")
                val result = FlightAccess().searchFlights(from, to, departTime, qty, cabinClass) ?: emptyList()

                val json = result.joinToString(prefix = "[", postfix = "]") { f ->
                    """{"id":${f.id},"flightNumber":"${f.flightNumber}","from":"${f.departureAirport}","to":"${f.arrivalAirport}","price":${f.price}}"""
                }

                call.respondText(json, ContentType.Application.Json)
            }

            post("/signup") {
                val params = call.receiveParameters()

                val name = params["name"]
                    ?: return@post call.respondText("Missing name", status = HttpStatusCode.BadRequest)
                val email = params["email"]
                    ?: return@post call.respondText("Missing email", status = HttpStatusCode.BadRequest)
                val password = params["password"]
                    ?: return@post call.respondText("Missing password", status = HttpStatusCode.BadRequest)

                val role = params["role"] ?: "user"

                val ok = UserAccess().createUser(name, email, password, role)

                if (ok) {
                    call.respondRedirect("/login.html")
                } else {
                    call.respondText("Email already exists", status = HttpStatusCode.Conflict)
                }
            }

            post("/login") {
                val params = call.receiveParameters()

                val email = params["email"]
                    ?: return@post call.respondText("Missing email", status = HttpStatusCode.BadRequest)
                val password = params["password"]
                    ?: return@post call.respondText("Missing password", status = HttpStatusCode.BadRequest)

                val userAccess = UserAccess()
                val ok = userAccess.checkLogin(email, password)

                if (ok) {
                    call.respondRedirect("/home.html")
                } else {
                    call.respondText("Invalid email or password", status = HttpStatusCode.Unauthorized)
                }
            }
        }
    }.start(wait = true)
}