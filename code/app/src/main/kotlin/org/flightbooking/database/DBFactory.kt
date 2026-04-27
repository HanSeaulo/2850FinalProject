package database

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import tables.*
import java.io.File

object DBFactory {
    fun init() {
        val dbFile = File("src/main/kotlin/org/flightbooking/database/resources/Database.db")
        val dbPath = dbFile.absolutePath

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