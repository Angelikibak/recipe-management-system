package recipes.service;

import recipes.database.RecipeRepository;
import recipes.database.StepRepository;
import recipes.model.ExecutionSession;
import recipes.model.Recipe;
import recipes.model.Step;

import java.util.List;

public class ExecutionService {

    private final RecipeRepository recipeRepo = new RecipeRepository();
    private final StepRepository stepRepo = new StepRepository();

    public ExecutionSession startExecution(int recipeId) throws Exception {
        Recipe recipe = recipeRepo.findById(recipeId);
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe not found: " + recipeId);
        }

        List<Step> steps = stepRepo.findByRecipeId(recipeId);
        if (steps.isEmpty()) {
            throw new IllegalStateException("Recipe has no steps");
        }

        return new ExecutionSession(recipe, steps);
    }
}
