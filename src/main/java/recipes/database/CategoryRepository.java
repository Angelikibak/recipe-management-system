package recipes.database;

import recipes.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    public List<Category> findAll() throws Exception {
        String sql = "SELECT id, name FROM category ORDER BY name ASC";
        List<Category> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        }
        return result;
    }
}
