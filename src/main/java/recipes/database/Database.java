package recipes.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

public class Database {

    // Η SQLite βάση θα είναι ένα αρχείο μέσα στο project folder όταν τρέχεις την εφαρμογή
    private static final String DB_URL = "jdbc:sqlite:recipes.db";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initialize() throws Exception {
        String schemaSql = loadResourceAsString("db/schema.sql");

        // Εκτέλεση όλων των CREATE TABLE statements
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Split με βάση το ; ώστε να τρέξουμε κάθε statement ξεχωριστά
            for (String raw : schemaSql.split(";")) {
                String sql = raw.trim();
                if (!sql.isEmpty()) {
                    stmt.execute(sql);
                }
            }
        }
    }

    private static String loadResourceAsString(String resourcePath) throws Exception {
        InputStream is = Database.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalStateException("Resource not found: " + resourcePath);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining("\n"));
        }
    }
}
