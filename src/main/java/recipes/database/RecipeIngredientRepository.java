package recipes.database;

import recipes.model.StepIngredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeIngredientRepository {

    public List<StepIngredient> findByRecipeId(int recipeId) throws Exception {
        String sql = """
            SELECT ri.recipe_id AS step_id, ri.ingredient_id, i.name AS ingredient_name,
                   ri.quantity, ri.unit, ri.note
            FROM recipe_ingredient ri
            JOIN ingredient i ON i.id = ri.ingredient_id
            WHERE ri.recipe_id = ?
            ORDER BY i.name ASC
            """;

        List<StepIngredient> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recipeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // χρησιμοποιούμε StepIngredient σαν "IngredientLine" για να μην φτιάχνουμε νέο model
                    result.add(new StepIngredient(
                            rs.getInt("step_id"), // εδώ κρατάμε recipeId στο stepId field
                            rs.getInt("ingredient_id"),
                            rs.getString("ingredient_name"),
                            (Double) rs.getObject("quantity"),
                            rs.getString("unit"),
                            rs.getString("note")
                    ));
                }
            }
        }
        return result;
    }

    public void upsert(int recipeId, int ingredientId, Double quantity, String unit, String note) throws Exception {
        String sql = """
            INSERT OR REPLACE INTO recipe_ingredient(recipe_id, ingredient_id, quantity, unit, note)
            VALUES(?, ?, ?, ?, ?)
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recipeId);
            ps.setInt(2, ingredientId);
            ps.setObject(3, quantity);
            ps.setString(4, unit);
            ps.setString(5, note);
            ps.executeUpdate();
        }
    }

    public void delete(int recipeId, int ingredientId) throws Exception {
        String sql = "DELETE FROM recipe_ingredient WHERE recipe_id = ? AND ingredient_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recipeId);
            ps.setInt(2, ingredientId);
            ps.executeUpdate();
        }
    }
}

