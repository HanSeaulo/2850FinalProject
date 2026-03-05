package database

import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils

import tables.*

object DBFactory {
    fun init() {
        // Use absolute path based on project structure
        val dbPath = "/workspaces/2850FinalProject/code/app/src/main/kotlin/org/flightbooking/database/resources/Database.db"
        
        Database.connect(
            url = "jdbc:sqlite:$dbPath",
            driver = "org.sqlite.JDBC"
        )
    

        println("Checking if tables exist...")
        transaction {
            // create the tables if they dont already exist
            SchemaUtils.create(
                ReportsTable, 
                UsersTable, 
                FlightsTable, 
                BookingsTable, 
                PassengersTable,
                RequestsTable
            )
        }
        println("All tables present")
    }
}