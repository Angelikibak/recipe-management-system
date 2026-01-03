package recipes.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExecutionSession {

    private final Recipe recipe;
    private final List<Step> steps;

    private int currentIndex = 0;
    private final Set<Integer> completedStepIds = new HashSet<>();

    public ExecutionSession(Recipe recipe, List<Step> steps) {
        this.recipe = recipe;
        this.steps = steps;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public Step getCurrentStep() {
        if (currentIndex >= steps.size()) return null;
        return steps.get(currentIndex);
    }

    public void markCurrentStepCompleted() {
        Step current = getCurrentStep();
        if (current == null) return;

        if (current.getId() != null) {
            completedStepIds.add(current.getId());
        }
        currentIndex++;
    }

    public boolean isFinished() {
        return currentIndex >= steps.size();
    }

    public int getProgressPercent() {
        int total = recipe.getTotalDurationMinutes();
        if (total <= 0) return 0;

        int completedMinutes = 0;
        for (Step s : steps) {
            if (s.getId() != null && completedStepIds.contains(s.getId())) {
                completedMinutes += s.getDurationMinutes();
            }
        }

        int percent = (int) Math.round((completedMinutes * 100.0) / total);
        if (percent < 0) percent = 0;
        if (percent > 100) percent = 100;
        return percent;
    }

    public int getCompletedMinutes() {
        int completedMinutes = 0;
        for (Step s : steps) {
            if (s.getId() != null && completedStepIds.contains(s.getId())) {
                completedMinutes += s.getDurationMinutes();
            }
        }
        return completedMinutes;
    }
}
