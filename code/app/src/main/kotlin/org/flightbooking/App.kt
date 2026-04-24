package org.flightbooking
import access.FlightAccess
import io.ktor.server.pebble.Pebble
import io.ktor.server.pebble.PebbleContent
import io.pebbletemplates.pebble.loader.ClasspathLoader
import access.UserAccess
import database.DBFactory
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

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
                    call.sessions.set(UserSession(name))   // ✅ AUTO LOGIN
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

                val userAccess = UserAccess()
                val user = userAccess.getUserByEmail(email)

                if (user != null && user.password == password) {
                    call.sessions.set(UserSession(user.name))   // ✅ SAVE SESSION
                    call.respondRedirect("/home.html")
                } else {
                    call.respondText("Wrong email or password") // ✅ FIXED MESSAGE
                }
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
            staticResources("/", "static")
        }
    }.start(wait = true)
}