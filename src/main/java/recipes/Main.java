package recipes;

import recipes.database.Database;
import recipes.database.RecipeRepository;
import recipes.model.Recipe;

public class Main {
    public static void main(String[] args) {
        try {
            Database.initialize();

            RecipeRepository repo = new RecipeRepository();

            Recipe recipe = new Recipe(
                    "Carbonara",
                    "Boil pasta, cook pancetta, mix with eggs & cheese.",
                    "EASY",
                    20
            );

            int id = repo.save(recipe);
            System.out.println("Saved recipe with id: " + id);

            System.out.println("All recipes:");
            for (Recipe r : repo.findAll()) {
                System.out.println(r);
            }

            Recipe loaded = repo.findById(id);
            System.out.println("Loaded by id: " + loaded);

            loaded = new Recipe(id, "Carbonara Updated", loaded.getDescription(), "MEDIUM", 25);
            repo.update(loaded);
            System.out.println("After update: " + repo.findById(id));

            repo.deleteById(id);
            System.out.println("After delete: " + repo.findById(id));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
