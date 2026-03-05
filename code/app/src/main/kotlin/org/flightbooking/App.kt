package org.flightbooking

import io.ktor.http.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.http.content.*
import database.DBFactory
import access.FlightAccess

fun main() {
    embeddedServer(Netty, port = 8080) {

        install(ContentNegotiation) {
            json()
        }

        DBFactory.init()

        routing {
            // So it also gets the files from 'app/src/main/resources/static' the static folder
            staticResources("/", "static")

            get("/") {
                call.respondRedirect("/home.html")
            }


            get("/search") {
                call.respondRedirect("/search.html")
            }
            
            get("/flights") {
                val flights = FlightAccess().getAll()
                val json = flights.joinToString(prefix = "[", postfix = "]") { f ->
                    """{"flightNumber":"${f.flightNumber}","from":"${f.departureAirport}","to":"${f.arrivalAirport}","price":${f.price}}"""
                }
                call.respondText(json, ContentType.Application.Json)
            }

            get("/report") {
                call.respondRedirect("/report.html")
            }

            get("/management") {
                call.respondRedirect("/management.html")
                }
        }

    }.start(wait = true)
}