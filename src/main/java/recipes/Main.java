package recipes;

import recipes.database.Database;
import recipes.ui.RecipeManagementFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            Database.initialize();
            SwingUtilities.invokeLater(() -> {
                RecipeManagementFrame frame = new RecipeManagementFrame();
                frame.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
