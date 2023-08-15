package me.werfrag;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private final String url;
    private Connection connection;

    public Database(String url) {
        this.url = url;
        connect();
        createTables(); // Chiamata per creare eventuali tabelle necessarie
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo per creare le tabelle nel database
    private void createTables() {
        try {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS staffmode (" +
                    "player_uuid TEXT PRIMARY KEY," +
                    "inventory_contents BLOB," +
                    "armor_contents BLOB" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}