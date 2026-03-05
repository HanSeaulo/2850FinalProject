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

import database.DBFactory
import access.FlightAccess

fun FlightsTest() {
    println("Establishing DB Connection...")
    DBFactory.init()
    println("Connection successful")

    val flightAccess = FlightAccess()
    val getall = flightAccess.searchFlights("LHR", "JFK")
    //val getall = flightAccess.getAll()

    println("All data in flights table: ")
    println(getall.joinToString())


}

import io.ktor.server.request.receiveParameters
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText


import org.flightbooking.database.UserAccess
import org.flightbooking.database.DBFactory



fun main() {
    embeddedServer(Netty, port = 8080) {
        DBFactory.init()

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
            post("/signup") {
                val params = call.receiveParameters()

                val name = params["name"] ?: return@post call.respondText("Missing name", status = HttpStatusCode.BadRequest)
                val email = params["email"] ?: return@post call.respondText("Missing email", status = HttpStatusCode.BadRequest)
                val password = params["password"] ?: return@post call.respondText("Missing password", status = HttpStatusCode.BadRequest)

                // role from form (or default)
                val role = params["role"] ?: "user"

                val ok = UserAccess.createUser(name, email, password, role)

                if (ok) {
                // simplest: redirect user to login page
                call.respondText("Signup successful. Go to /login.html", status = HttpStatusCode.Created)
                // better UX later: respondRedirect("/login.html")
                } else {
                    call.respondText("Email already exists", status = HttpStatusCode.Conflict)
                }
            }

        post("/login") {
            val params = call.receiveParameters()

            val email = params["email"] ?: return@post call.respondText("Missing email", status = HttpStatusCode.BadRequest)
            val password = params["password"] ?: return@post call.respondText("Missing password", status = HttpStatusCode.BadRequest)

            val ok = UserAccess.checkLogin(email, password)

            if (ok) {
                call.respondText("Login successful. Go to /home.html", status = HttpStatusCode.OK)
                // better UX later: respondRedirect("/home.html")
            } else {
                call.respondText("Invalid email or password", status = HttpStatusCode.Unauthorized)
            }
        }
             }

    }.start(wait = true)
}