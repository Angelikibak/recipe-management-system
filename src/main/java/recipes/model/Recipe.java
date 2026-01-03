package recipes.model;

public class Recipe {
    private Integer id;
    private String name;
    private String description;
    private String difficulty; // EASY / MEDIUM / HARD
    private int totalDurationMinutes;
    private Integer categoryId;

    public Recipe(Integer id, String name, String description, String difficulty, int totalDurationMinutes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.totalDurationMinutes = totalDurationMinutes;
    }

    public Recipe(String name, String description, String difficulty, int totalDurationMinutes) {
        this(null, name, description, difficulty, totalDurationMinutes);
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getDifficulty() { return difficulty; }
    public int getTotalDurationMinutes() { return totalDurationMinutes; }
    public Integer getCategoryId() { return categoryId; }

    public void setId(Integer id) { this.id = id; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", totalDurationMinutes=" + totalDurationMinutes +
                '}';
    }
}
