package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {

    public static String getPropertyString() {
        Properties props = new Properties();
        try (InputStream input = PropertyUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find db.properties");
            }
            props.load(input);

            String host = props.getProperty("host");
            String port = props.getProperty("port");
            String dbname = props.getProperty("dbname");
            String username = props.getProperty("username");
            String password = props.getProperty("password");

            return "jdbc:mysql://" + host + ":" + port + "/" + dbname +
                    "?user=" + username + "&password=" + password;

        } catch (IOException e) {
            throw new RuntimeException("Error loading db.properties", e);
        }
    }
}
