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
            get("/management") {
    call.respondText(managementPageHtml(), contentType = ContentType.Text.Html)
}

get("/management/flights") {
    call.respondText("<h1>Manage Flights (Coming Soon)</h1>", contentType = ContentType.Text.Html)
}
get("/management/bookings") {
    call.respondText("<h1>Manage Bookings (Coming Soon)</h1>", contentType = ContentType.Text.Html)
}
get("/management/users") {
    call.respondText("<h1>Manage Users (Coming Soon)</h1>", contentType = ContentType.Text.Html)
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
  <link rel="stylesheet" href="/static/style.css" />
</head>
<body>

  <!-- Background Layers -->
  <div class="bg bg-a" id="bgA"></div>
  <div class="bg bg-b" id="bgB"></div>

  <header class="navbar">
    <div class="nav-container">
      <div class="logo">✈ Go Fly</div>
      <nav>
        <a href="/">Home</a>
        <a href="/bookings">View Bookings</a>
        <a href="/signin">Sign In</a>
        <a href="/management">Management</a>
      </nav>
    </div>
  </header>

  <main class="hero">
    <div class="features">
      <div class="feature-card">
        <h3>Best Prices</h3>
        <p>Competitive fares worldwide</p>
      </div>
      <div class="feature-card">
        <h3>Secure Booking</h3>
        <p>Safe and encrypted payments.</p>
      </div>
    </div>

    <a href="/flights" class="big-button">
      MAKE A BOOKING
    </a>
  </main>

  <script>
    const images = [
      "/static/bg/bg1.jpeg",
      "/static/bg/bg2.jpeg",
      "/static/bg/bg3.jpeg"
    ];

    let index = 0;
    let showingA = true;

    const bgA = document.getElementById("bgA");
    const bgB = document.getElementById("bgB");

    bgA.style.backgroundImage = `url('${"$"}{images[0]}')`;
    bgA.classList.add("show");

    setInterval(() => {
      index = (index + 1) % images.length;

      const next = images[index];
      const showEl = showingA ? bgB : bgA;
      const hideEl = showingA ? bgA : bgB;

      showEl.style.backgroundImage = `url('${"$"}{next}')`;
      showEl.classList.add("show");
      hideEl.classList.remove("show");

      showingA = !showingA;
    }, 6000);
  </script>

</body>
</html>
""".trimIndent()

private fun managementPageHtml(): String = """
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Management - Flight Booking System</title>
  <link rel="stylesheet" href="/static/style.css" />
</head>
<body>

  <header class="navbar">
    <div class="nav-container">
      <div class="logo">✈ Flight Booker</div>
      <nav>
        <a href="/">Home</a>
        <a href="/bookings">View Bookings</a>
        <a href="/signin">Sign In</a>
        <a href="/management">Management</a>
      </nav>
    </div>
  </header>

  <main class="page container">
    <div class="page-header">
      <div>
        <h1 class="page-title">Management Dashboard</h1>
        <p class="page-subtitle">Admin tools for flights, bookings, and users.</p>
      </div>
      <a class="button-outline" href="/">Back to Home</a>
    </div>

    <section class="stats">
      <div class="stat-card">
        <div class="stat-label">Today’s Bookings</div>
        <div class="stat-value">12</div>
        <div class="stat-note">+2 vs yesterday</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">Active Flights</div>
        <div class="stat-value">48</div>
        <div class="stat-note">Across all routes</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">Registered Users</div>
        <div class="stat-value">305</div>
        <div class="stat-note">Growing steadily</div>
      </div>
    </section>

    <section class="card">
      <h2 class="section-title">Quick Actions</h2>
      <div class="action-grid">
        <a class="action-card" href="/management/flights">
          <h3>Manage Flights</h3>
          <p>Create routes, update prices, set schedules.</p>
          <span class="chip">Flights</span>
        </a>

        <a class="action-card" href="/management/bookings">
          <h3>Manage Bookings</h3>
          <p>Review, cancel, and confirm reservations.</p>
          <span class="chip">Bookings</span>
        </a>

        <a class="action-card" href="/management/users">
          <h3>Manage Users</h3>
          <p>View users, roles, and permissions.</p>
          <span class="chip">Users</span>
        </a>
      </div>
    </section>

    <section class="card">
      <div class="table-header">
        <h2 class="section-title">Recent Activity</h2>
        <span class="muted">Demo data for now</span>
      </div>

      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>Time</th>
              <th>Action</th>
              <th>Details</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>10:42</td>
              <td>Booking created</td>
              <td>#BK1029 • YVR → YYZ</td>
              <td><span class="badge ok">OK</span></td>
            </tr>
            <tr>
              <td>10:10</td>
              <td>Flight updated</td>
              <td>AC101 price changed</td>
              <td><span class="badge warn">Review</span></td>
            </tr>
            <tr>
              <td>09:55</td>
              <td>User registered</td>
              <td>newuser@email.com</td>
              <td><span class="badge ok">OK</span></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>

</body>
</html>
""".trimIndent()