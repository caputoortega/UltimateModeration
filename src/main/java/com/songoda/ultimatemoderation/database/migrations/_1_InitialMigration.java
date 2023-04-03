package com.songoda.ultimatemoderation.database.migrations;

import com.songoda.core.database.DataMigration;
import com.songoda.core.database.MySQLConnector;
import com.songoda.ultimatemoderation.UltimateModeration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _1_InitialMigration extends DataMigration {

    public _1_InitialMigration() {
        super(2);
    }

    @Override
    public void migrate(Connection connection, String tablePrefix) throws SQLException {
        String autoIncrement = UltimateModeration.getInstance().getDatabaseConnector() instanceof MySQLConnector ? " AUTO_INCREMENT" : "";

        // Create templates table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "templates (" +
                    "id INTEGER PRIMARY KEY" + autoIncrement + ", " +
                    "punishment_type VARCHAR(15) NOT NULL, " +
                    "duration BIGINT NOT NULL," + // If -1 then its permanent
                    "reason TEXT," + // If null then no reason is given
                    "name VARCHAR(100), " +
                    "creator VARCHAR(36) NOT NULL" +
                    ")");
        }

        // Create punishments table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "punishments (" +
                    "id INTEGER PRIMARY KEY" + autoIncrement + ", " +
                    "type VARCHAR(15) NOT NULL, " +
                    "duration BIGINT," + // If null then its permanent
                    "reason TEXT," + // If null then no reason is given
                    "victim VARCHAR(36) NOT NULL," +
                    "punisher VARCHAR(36) NOT NULL," +
                    "expiration BIGINT" + // If null then its permanent
                    ")");
        }

        // Create notes table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "notes (" +
                    "id INTEGER PRIMARY KEY" + autoIncrement + ", " +
                    "note TEXT NOT NULL, " +
                    "author VARCHAR(36) NOT NULL," +
                    "subject VARCHAR (36) NOT NULL," +
                    "creation BIGINT" +
                    ")");
        }

        // Create tickets table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "tickets (" +
                    "id INTEGER PRIMARY KEY" + autoIncrement + ", " +
                    "victim VARCHAR(36) NOT NULL," +
                    "subject TEXT NOT NULL," +
                    "type VARCHAR(50) NOT NULL, " +
                    "status VARCHAR(10) NOT NULL, " +
                    "world TEXT NOT NULL, " +
                    "x DOUBLE NOT NULL, " +
                    "y DOUBLE NOT NULL, " +
                    "z DOUBLE NOT NULL, " +
                    "pitch FLOAT NOT NULL, " +
                    "yaw FLOAT NOT NULL " +
                    ")");
        }

        // Create ticket responses table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "ticket_responses (" +
                    "ticket_id INTEGER NOT NULL, " +
                    "author VARCHAR(36) NOT NULL," +
                    "message TEXT NOT NULL," +
                    "posted_date BIGINT" +
                    ")");
        }

        // Create IP history table
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE IF NOT EXISTS " + tablePrefix + "ip_history (" +
                "ip VARCHAR(255) NOT NULL, " +
                "player VARCHAR(36) NOT NULL, " +
                "lastUsed TIMESTAMP NOT NULL" +
                ");"
            );
        }
    }

}
