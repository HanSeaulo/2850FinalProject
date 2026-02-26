package database

import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils

import tables.*

object DBFactory {
    fun init() {
        Database.connect(
            url = "jdbc:sqlite:code/app/src/main/kotlin/org/flightbooking/database/resources/Database.db",
            driver = "org.sqlite.JDBC"
        )

        println("Checking if tables exist...")
        transaction {
            // create the tables if they dont already exist
            SchemaUtils.create(
                Reports, 
                Users, 
                Flights, 
                Bookings, 
                Passengers,
                Requests
            )
        }
        println("All tables present")
    }
}