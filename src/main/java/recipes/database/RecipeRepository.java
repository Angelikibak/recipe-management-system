package recipes.database;

import recipes.model.Recipe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeRepository {

    public int save(Recipe recipe) throws Exception {
        String sql = "INSERT INTO recipe (name, description, difficulty, total_duration_minutes, category_id, photo_path) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, recipe.getName());
            ps.setString(2, recipe.getDescription());
            ps.setString(3, recipe.getDifficulty());
            ps.setInt(4, recipe.getTotalDurationMinutes());
            ps.setObject(5, recipe.getCategoryId());     // μπορεί να είναι null
            ps.setString(6, recipe.getPhotoPath());      // μπορεί να είναι null

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
        String sql = "SELECT id, name, description, difficulty, total_duration_minutes, category_id, photo_path " +
                "FROM recipe ORDER BY id DESC";

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

                r.setCategoryId((Integer) rs.getObject("category_id")); // μπορεί να είναι NULL
                r.setPhotoPath(rs.getString("photo_path"));             // μπορεί να είναι NULL

                result.add(r);
            }
        }

        return result;
    }

    public Recipe findById(int id) throws Exception {
        String sql = "SELECT id, name, description, difficulty, total_duration_minutes, category_id, photo_path " +
                "FROM recipe WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Recipe r = new Recipe(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("difficulty"),
                            rs.getInt("total_duration_minutes")
                    );

                    r.setCategoryId((Integer) rs.getObject("category_id"));
                    r.setPhotoPath(rs.getString("photo_path"));

                    return r;
                }
            }
        }

        return null;
    }

    public void deleteById(int recipeId) throws Exception {
        String deleteStepIngredients =
                "DELETE FROM step_ingredient WHERE step_id IN (SELECT id FROM step WHERE recipe_id = ?)";

        String deleteSteps =
                "DELETE FROM step WHERE recipe_id = ?";

        String deleteRecipe =
                "DELETE FROM recipe WHERE id = ?";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(deleteStepIngredients);
                 PreparedStatement ps2 = conn.prepareStatement(deleteSteps);
                 PreparedStatement ps3 = conn.prepareStatement(deleteRecipe)) {

                ps1.setInt(1, recipeId);
                ps1.executeUpdate();

                ps2.setInt(1, recipeId);
                ps2.executeUpdate();

                ps3.setInt(1, recipeId);
                ps3.executeUpdate();

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean update(Recipe recipe) throws Exception {
        if (recipe.getId() == null) {
            throw new IllegalArgumentException("Recipe id is required for update");
        }

        String sql = "UPDATE recipe " +
                "SET name = ?, description = ?, difficulty = ?, total_duration_minutes = ?, category_id = ?, photo_path = ? " +
                "WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, recipe.getName());
            ps.setString(2, recipe.getDescription());
            ps.setString(3, recipe.getDifficulty());
            ps.setInt(4, recipe.getTotalDurationMinutes());
            ps.setObject(5, recipe.getCategoryId());   // μπορεί να είναι null
            ps.setString(6, recipe.getPhotoPath());    // μπορεί να είναι null
            ps.setInt(7, recipe.getId());

            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }
}
