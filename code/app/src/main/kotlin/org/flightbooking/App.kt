package org.flightbooking

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun main() {
    embeddedServer(Netty, port = 8080) {

        install(ContentNegotiation) {
            json()
        }

        routing {
            get("/") {
                call.respondText("Ktor is running!")
            }
            get("/flights") {
                call.respondText(
                    """[{"flightNumber":"AC101","from":"YVR","to":"YYZ","price":350}]""",
                    contentType = io.ktor.http.ContentType.Application.Json
                )
            }
        }

    }.start(wait = true)
}