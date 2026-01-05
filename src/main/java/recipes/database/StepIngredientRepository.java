package recipes.database;

import recipes.model.StepIngredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StepIngredientRepository {

    public List<StepIngredient> findByStepId(int stepId) throws Exception {
        String sql = """
            SELECT si.step_id, si.ingredient_id, i.name AS ingredient_name,
                   si.quantity, si.unit, si.note
            FROM step_ingredient si
            JOIN ingredient i ON i.id = si.ingredient_id
            WHERE si.step_id = ?
            ORDER BY i.name ASC
            """;

        List<StepIngredient> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, stepId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new StepIngredient(
                            rs.getInt("step_id"),
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

    public void upsert(int stepId, int ingredientId, Double quantity, String unit, String note) throws Exception {
        // SQLite: INSERT OR REPLACE δουλεύει λόγω PK(step_id, ingredient_id)
        String sql = """
            INSERT OR REPLACE INTO step_ingredient(step_id, ingredient_id, quantity, unit, note)
            VALUES(?, ?, ?, ?, ?)
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, stepId);
            ps.setInt(2, ingredientId);
            ps.setObject(3, quantity);
            ps.setString(4, unit);
            ps.setString(5, note);

            ps.executeUpdate();
        }
    }

    public void delete(int stepId, int ingredientId) throws Exception {
        String sql = "DELETE FROM step_ingredient WHERE step_id = ? AND ingredient_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, stepId);
            ps.setInt(2, ingredientId);
            ps.executeUpdate();
        }
    }
}
