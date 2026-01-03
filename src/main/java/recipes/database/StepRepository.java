package recipes.database;

import recipes.model.Step;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StepRepository {

    public int save(Step step) throws Exception {
        String sql = "INSERT INTO step (recipe_id, sequence_number, title, description, duration_minutes, photo_path) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, step.getRecipeId());
            ps.setInt(2, step.getSequenceNumber());
            ps.setString(3, step.getTitle());
            ps.setString(4, step.getDescription());
            ps.setInt(5, step.getDurationMinutes());
            ps.setString(6, step.getPhotoPath()); // μπορεί να είναι null

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    step.setId(id);
                    return id;
                }
            }
        }

        throw new SQLException("Could not retrieve generated id for step");
    }

    public List<Step> findByRecipeId(int recipeId) throws Exception {
        String sql = "SELECT id, recipe_id, sequence_number, title, description, duration_minutes, photo_path " +
                "FROM step WHERE recipe_id = ? ORDER BY sequence_number ASC";

        List<Step> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recipeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Step s = new Step(
                            rs.getInt("id"),
                            rs.getInt("recipe_id"),
                            rs.getInt("sequence_number"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getInt("duration_minutes")
                    );

                    s.setPhotoPath(rs.getString("photo_path"));
                    result.add(s);
                }
            }
        }

        return result;
    }

    public void update(Step step) throws Exception {
        if (step.getId() == null) {
            throw new IllegalArgumentException("Step id is required for update");
        }

        String sql = "UPDATE step " +
                "SET sequence_number = ?, title = ?, description = ?, duration_minutes = ?, photo_path = ? " +
                "WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, step.getSequenceNumber());
            ps.setString(2, step.getTitle());
            ps.setString(3, step.getDescription());
            ps.setInt(4, step.getDurationMinutes());
            ps.setString(5, step.getPhotoPath()); // μπορεί να είναι null
            ps.setInt(6, step.getId());

            ps.executeUpdate();
        }
    }

    public void deleteById(int stepId) throws Exception {
        String sql = "DELETE FROM step WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, stepId);
            ps.executeUpdate();
        }
    }
}
