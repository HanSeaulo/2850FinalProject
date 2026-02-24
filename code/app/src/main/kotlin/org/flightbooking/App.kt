package org.flightbooking

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*

fun main() {
    embeddedServer(Netty, port = 8080) {

        install(ContentNegotiation) {
            json()
        }

        routing {
            static("/static") {
                resources("static")
            }

            get("/") {
                call.respondText(homePageHtml(), contentType = ContentType.Text.Html)
            }

            get("/flights") {
                call.respondText(
                    """[{"flightNumber":"AC101","from":"YVR","to":"YYZ","price":350}]""",
                    contentType = ContentType.Application.Json
                )
            }

            get("/health") {
                call.respondText("OK", contentType = ContentType.Text.Plain)
            }
        }

    }.start(wait = true)
}

private fun homePageHtml(): String = """
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Flight Booking System</title>
  // refer to css files
  <link rel="stylesheet" href="/static/style.css" />
</head>
<body>

  <header class="navbar">
    <div class="nav-container">
      <div class="logo">✈ Go Fly</div>
      <nav>
        <a href="/">Home</a>
        <a href="/bookings">View Bookings</a>
        <a href="/signin">Sign In</a>
      </nav>
    </div>
  </header>

  <main class="hero">

    <a href="/flights" class="big-button">
      MAKE A BOOKING
    </a>
  </main>

</body>
</html>
""".trimIndent()