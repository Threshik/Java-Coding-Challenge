package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                String url = PropertyUtil.getPropertyString();
                connection = DriverManager.getConnection(url);
                // System.out.println("Connected to MySQL successfully!");
            } catch (SQLException e) {
                System.out.println("Connection failed!");
                e.printStackTrace();
            }
        }
        return connection;
    }
}



