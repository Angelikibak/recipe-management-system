package recipes.model;

public class StepIngredient {
    private int stepId;
    private int ingredientId;
    private String ingredientName;
    private Double quantity;   // μπορεί να είναι null
    private String unit;       // μπορεί να είναι null
    private String note;       // μπορεί να είναι null

    public StepIngredient(int stepId, int ingredientId, String ingredientName,
                          Double quantity, String unit, String note) {
        this.stepId = stepId;
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unit = unit;
        this.note = note;
    }

    public int getStepId() { return stepId; }
    public int getIngredientId() { return ingredientId; }
    public String getIngredientName() { return ingredientName; }
    public Double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getNote() { return note; }

    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setNote(String note) { this.note = note; }

    @Override
    public String toString() {
        String q = (quantity == null) ? "" : String.valueOf(quantity);
        String u = (unit == null) ? "" : unit;
        return ingredientName + " " + q + " " + u;
    }
}
