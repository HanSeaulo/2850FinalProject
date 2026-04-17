package org.flightbooking

import io.ktor.server.pebble.*
import io.pebbletemplates.pebble.loader.ClasspathLoader
import io.ktor.http.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.http.content.*
import kotlinx.datetime.LocalDateTime

import database.DBFactory
import access.*

fun main() {
    embeddedServer(Netty, port = 8080) {

        DBFactory.init()

        install(ContentNegotiation) {
            json()
        }

        // 🔥 FIXED PEBBLE CONFIG
        install(Pebble) {
            loader(
                ClasspathLoader().apply {
                    prefix = "templates"
                }
            )
        }

        routing {

            get("/") {
                call.respondRedirect("/home.html")
            }

            get("/login") {
                call.respondRedirect("/login.html")
            }

            get("/signup") {
                call.respondRedirect("/signup.html")
            }

            // 🔥 MAIN FIXED ROUTE
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

            get("/flights") {
                val from = call.request.queryParameters["from"] ?: ""
                val to = call.request.queryParameters["to"] ?: ""
                val depart = call.request.queryParameters["depart"] ?: ""
                val qty = call.request.queryParameters["qty"]?.toIntOrNull() ?: 1
                val cabinRaw = call.request.queryParameters["class"] ?: "econ"

                val cabinClass = if (cabinRaw == "bus") "Business" else "Economy"

                if (from.isBlank() || to.isBlank() || depart.isBlank()) {
                    call.respondText("Missing search parameters", status = HttpStatusCode.BadRequest)
                    return@get
                }

                val departTime = LocalDateTime.parse("${depart}T00:00:00")
                val flights = FlightAccess().searchFlights(from, to, departTime, qty, cabinClass) ?: emptyList()

                call.respond(PebbleContent("flights.peb", mapOf("flights" to flights)))
            }

            // MUST BE LAST
            staticResources("/", "static")
        }

    }.start(wait = true)
}