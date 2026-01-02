package recipes.database;

import recipes.model.Recipe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeRepository {

    public int save(Recipe recipe) throws Exception {
        String sql = "INSERT INTO recipe (name, description, difficulty, total_duration_minutes) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, recipe.getName());
            ps.setString(2, recipe.getDescription());
            ps.setString(3, recipe.getDifficulty());
            ps.setInt(4, recipe.getTotalDurationMinutes());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    recipe.setId(id);
                    return id;
                }
            }
        }

        throw new SQLException("Could not retrieve generated id for recipe");
    }

    public List<Recipe> findAll() throws Exception {
        String sql = "SELECT id, name, description, difficulty, total_duration_minutes FROM recipe ORDER BY id DESC";
        List<Recipe> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Recipe r = new Recipe(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("difficulty"),
                        rs.getInt("total_duration_minutes")
                );
                result.add(r);
            }
        }

        return result;
    }

    public Recipe findById(int id) throws Exception {
        String sql = "SELECT id, name, description, difficulty, total_duration_minutes FROM recipe WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Recipe(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("difficulty"),
                            rs.getInt("total_duration_minutes")
                    );
                }
            }
        }
        return null;
    }

    public boolean deleteById(int id) throws Exception {
        String sql = "DELETE FROM recipe WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    public boolean update(Recipe recipe) throws Exception {
        if (recipe.getId() == null) {
            throw new IllegalArgumentException("Recipe id is required for update");
        }

        String sql = "UPDATE recipe SET name = ?, description = ?, difficulty = ?, total_duration_minutes = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, recipe.getName());
            ps.setString(2, recipe.getDescription());
            ps.setString(3, recipe.getDifficulty());
            ps.setInt(4, recipe.getTotalDurationMinutes());
            ps.setInt(5, recipe.getId());

            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }



}
