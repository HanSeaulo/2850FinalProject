package database

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import tables.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object DBFactory {
    private fun resolveDbPath(): String {
        val direct = Paths.get(
            System.getProperty("user.dir"),
            "app", "src", "main", "kotlin", "org", "flightbooking", "database", "resources", "Database.db"
        )
        if (Files.exists(direct)) return direct.toString()

        var current: Path = Paths.get(System.getProperty("user.dir")).toAbsolutePath()
        repeat(4) {
            val candidate = current.resolve(
                Paths.get("code", "app", "src", "main", "kotlin", "org", "flightbooking", "database", "resources", "Database.db")
            )
            if (Files.exists(candidate)) return candidate.toString()
            current = current.parent ?: return@repeat
        }

        return direct.toString()
    }

    fun init() {
        val dbPath = resolveDbPath()

        Database.connect(
            url = "jdbc:sqlite:$dbPath",
            driver = "org.sqlite.JDBC"
        )

        transaction {
            SchemaUtils.create(
                ReportsTable,
                UsersTable,
                FlightsTable,
                BookingsTable,
                PassengersTable,
                RequestsTable
            )
        }
    }
}