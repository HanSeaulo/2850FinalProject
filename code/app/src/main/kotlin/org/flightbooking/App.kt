package org.flightbooking

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.http.content.*

fun main() {
    embeddedServer(Netty, port = 8080) {

        install(ContentNegotiation) {
            json()
        }

        routing {
            // So it also gets the files from 'app/src/main/resources/static' the static folder
            staticResources("/", "static")

            get("/") {
                call.respondText("Ktor is running!")
            }

            get("/flights") {
                val from = call.request.queryParameters["from"]
                val to = call.request.queryParameters["to"]
                val departDate = call.request.queryParameters["departDate"]
                val passengers = call.request.queryParameters["passengers"]

                call.respondText(
                    """
                    {
                    "from": "$from",
                    "to": "$to",
                    "departDate": "$departDate",
                    "passengers": "$passengers"
                    }
                    """.trimIndent(),
                    contentType = io.ktor.http.ContentType.Application.Json
                )
            }

            get("/search") {
                call.respondRedirect("/search.html")
            }

            get("/report") {
                call.respondRedirect("/report.html")
            }
        }

    }.start(wait = true)
}