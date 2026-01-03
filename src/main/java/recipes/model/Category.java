package recipes.model;

public class Category {
    private Integer id;
    private String name;

    public Category(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name; // έτσι φαίνεται ωραία στο dropdown
    }
}
