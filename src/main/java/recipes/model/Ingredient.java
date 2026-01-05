package recipes.model;

public class Ingredient {
    private Integer id;
    private String name;

    public Ingredient(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public void setId(Integer id) { this.id = id; }

    @Override
    public String toString() {
        return name; // για combo box
    }
}
