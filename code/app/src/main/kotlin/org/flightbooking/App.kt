package org.flightbooking

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
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText

data class UserSession(val name: String, val email: String)

fun FlightsTest() {
    println("Establishing DB Connection...")
    DBFactory.init()
    println("Connection successful")

    val flightAccess = FlightAccess()
    val getall = flightAccess.searchFlights("LHR", "JFK")
    //val getall = flightAccess.getAll()

    println("All data in flights table: ")
    println(getall?.joinToString() ?: "No flights found")
    println(getall.joinToString())
}

fun main() {
    embeddedServer(Netty, port = 8080) {
        DBFactory.init()

        install(ContentNegotiation) {
            json()
        }

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
                call.respond(PebbleContent("templates/search.peb", mapOf("airports" to airports)))
            }

            get("/flights") {
                var from = ""
                var to = ""
                var depart = ""
                var qty = 1
                var cabinRaw = ""
                var cabinClass = "Economy"

                // checking if the values are null, if they are then we dont send null to the function and dont update the variables with them
                if (call.request.queryParameters["from"] != null) {
                    from = call.request.queryParameters["from"].toString()
                }
                if (call.request.queryParameters["to"] != null) {
                    to = call.request.queryParameters["to"].toString()
                }
                if (call.request.queryParameters["depart"] != null) {
                    depart = call.request.queryParameters["depart"].toString()
                }
                if (call.request.queryParameters["qty"] != null) {
                    qty = call.request.queryParameters["qty"]!!.toInt()
                }
                if (call.request.queryParameters["class"] != null) {
                    cabinRaw = call.request.queryParameters["class"].toString()
                }

                // we get back "econ" or "bus" in a short form, need to adjust it for the function
                if (cabinRaw == "econ") {
                    cabinClass = "Economy"
                }
                if (cabinRaw == "bus") {
                    cabinClass = "Business"
                }

                // fixing the type of the data for the searchFlights function
                val departTime = LocalDateTime.parse(depart + "T00:00:00")

                val result = FlightAccess().searchFlights(from, to, departTime, qty, cabinClass)
                var flights = emptyList<models.Flights>()
                if (result != null) {
                    flights = result
                }

                call.respond(PebbleContent("templates/flights.peb", mapOf("flights" to flights)))
            }

            get("/api/flights") {
                val flights = FlightAccess().getAll()
                val json = flights.joinToString(prefix = "[", postfix = "]") { f ->
                    """{"flightNumber":"${f.flightNumber}","from":"${f.departureAirport}","to":"${f.arrivalAirport}","price":${f.price}}"""
                }
                call.respondText(json, ContentType.Application.Json)
            }

            get("/report") {
                call.respond(PebbleContent("templates/report.peb", mapOf()))
            }

            get("/management") {
                call.respondRedirect("/management.html")
            }

            get("/current-user") {
                val session = call.sessions.get<UserSession>()

                if (session != null) {
                    call.respondText(session.name)
                } else {
                    call.respondText("")
                }
            }

            get("/logout") {
                call.sessions.clear<UserSession>()
                call.respondRedirect("/home.html")
            }
            get("/payment") {
    call.respondRedirect("/payment.html")
}

    get("/flight-price") {
        val flightId = call.request.queryParameters["flightId"]?.toIntOrNull()

        if (flightId == null) {
            call.respond(HttpStatusCode.BadRequest, """{"error":"Missing or invalid flightId"}""")
            return@get
        }

        val flight = FlightAccess().getFlightById(flightId)

        if (flight == null) {
            call.respond(HttpStatusCode.NotFound, """{"error":"Flight not found"}""")
        } else {
            call.respondText(
                """{"price":${flight.price}}""",
                ContentType.Application.Json
            )
        }
    }

            post("/signup") {
                val params = call.receiveParameters()

                val name = params["name"] ?: return@post call.respondText("Missing name", status = HttpStatusCode.BadRequest)
                val email = params["email"] ?: return@post call.respondText("Missing email", status = HttpStatusCode.BadRequest)
                val password = params["password"] ?: return@post call.respondText("Missing password", status = HttpStatusCode.BadRequest)

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

                val email = params["email"] ?: return@post call.respondText("Missing email", status = HttpStatusCode.BadRequest)
                val password = params["password"] ?: return@post call.respondText("Missing password", status = HttpStatusCode.BadRequest)

                val userAccess = UserAccess()
                val ok = userAccess.checkLogin(email, password)

                if (ok) {
                    val user = userAccess.getUserByEmail(email)

                    if (user != null) {
                        call.sessions.set(UserSession(user.name, user.email))
                        call.respondRedirect("/home.html")
                    } else {
                        call.respondText("User not found", status = HttpStatusCode.NotFound)
                    }
                } else {
                    call.respondText("Invalid email or password", status = HttpStatusCode.Unauthorized)
                }
            }
        }

    }.start(wait = true)
}