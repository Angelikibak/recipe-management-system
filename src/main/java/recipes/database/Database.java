package recipes.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;
import java.sql.SQLException;

public class Database {

    // Η SQLite βάση θα είναι ένα αρχείο μέσα στο project folder όταν τρέχεις την εφαρμογή
    private static final String DB_URL = "jdbc:sqlite:recipes.db";

    public static Connection getConnection() throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }


    public static void initialize() throws Exception {
        String schemaSql = loadResourceAsString("db/schema.sql");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            for (String raw : schemaSql.split(";")) {
                String sql = raw.trim();
                if (!sql.isEmpty()) {
                    stmt.execute(sql);
                }
            }

            // Migration: category_id
            try {
                stmt.execute("ALTER TABLE recipe ADD COLUMN category_id INTEGER REFERENCES category(id)");
            } catch (SQLException ignored) {
                // column already exists
            }
            // Migration: recipe photo
            try { stmt.execute("ALTER TABLE recipe ADD COLUMN photo_path TEXT"); }
            catch (SQLException ignored) {}

            // Migration: step photo (μία φωτο ανά βήμα)
            try { stmt.execute("ALTER TABLE step ADD COLUMN photo_path TEXT"); }
            catch (SQLException ignored) {}

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
