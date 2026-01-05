package recipes.database;

import recipes.model.Ingredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredientRepository {

    public List<Ingredient> findAll() throws Exception {
        String sql = "SELECT id, name FROM ingredient ORDER BY name ASC";
        List<Ingredient> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new Ingredient(rs.getInt("id"), rs.getString("name")));
            }
        }
        return result;
    }

    public Ingredient findByName(String name) throws Exception {
        String sql = "SELECT id, name FROM ingredient WHERE name = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Ingredient(rs.getInt("id"), rs.getString("name"));
            }
        }
        return null;
    }

    public int save(Ingredient ingredient) throws Exception {
        String sql = "INSERT INTO ingredient(name) VALUES(?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, ingredient.getName());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    ingredient.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Could not retrieve generated id for ingredient");
    }

    // Helper: παίρνει ή δημιουργεί ingredient
    public Ingredient getOrCreateByName(String rawName) throws Exception {
        String name = rawName.trim();
        if (name.isEmpty()) throw new IllegalArgumentException("Ingredient name is required");

        Ingredient existing = findByName(name);
        if (existing != null) return existing;

        Ingredient created = new Ingredient(null, name);
        save(created);
        return created;
    }
}
