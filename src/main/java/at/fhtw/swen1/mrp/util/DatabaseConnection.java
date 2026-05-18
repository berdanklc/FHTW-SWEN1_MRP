package at.fhtw.swen1.mrp.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Function;

public class DatabaseConnection {
    private static final String dbUrl;
    private static final String dbUsername;
    private static final String dbPassword;

    static {
        try {
            Class.forName("org.postgresql.Driver");

            Properties props = new Properties();
            InputStream input = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("application.properties");

            if (input != null) {
                props.load(input);
                input.close();
            }

            dbUrl = getConfigValue("DB_URL", "db.url", props);
            dbUsername = getConfigValue("DB_USERNAME", "db.username", props);
            dbPassword = getConfigValue("DB_PASSWORD", "db.password", props);

            if (dbUrl == null || dbUrl.isBlank()) {
                throw new RuntimeException("DB_URL bzw. db.url ist nicht gesetzt.");
            }

            if (dbUsername == null || dbUsername.isBlank()) {
                throw new RuntimeException("DB_USERNAME bzw. db.username ist nicht gesetzt.");
            }

            if (dbPassword == null || dbPassword.isBlank()) {
                throw new RuntimeException("DB_PASSWORD bzw. db.password ist nicht gesetzt.");
            }

            System.out.println("Datenbank-Konfiguration geladen");
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Laden der DB-Konfiguration: " + e.getMessage(), e);
        }
    }

    private static String getConfigValue(String environmentVariable, String propertyName, Properties props) {
        String valueFromEnvironment = System.getenv(environmentVariable);

        if (valueFromEnvironment != null && !valueFromEnvironment.isBlank()) {
            return valueFromEnvironment;
        }

        return props.getProperty(propertyName);
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (Exception e) {
            throw new RuntimeException("Datenbankverbindung fehlgeschlagen: " + e.getMessage(), e);
        }
    }

    public static <T> T executeInTransaction(Function<Connection, T> operation) {
        Connection conn = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            T result = operation.apply(conn);

            conn.commit();
            return result;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }

            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }

            throw new RuntimeException("Transaktion fehlgeschlagen: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public static void executeInTransactionVoid(TransactionConsumer operation) {
        executeInTransaction(conn -> {
            try {
                operation.accept(conn);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return null;
        });
    }

    @FunctionalInterface
    public interface TransactionConsumer {
        void accept(Connection conn) throws SQLException;
    }
}