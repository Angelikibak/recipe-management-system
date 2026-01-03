package recipes.model;

public class Step {
    private Integer id;
    private int recipeId;
    private int sequenceNumber;
    private String title;
    private String description;
    private int durationMinutes;
    private String photoPath;




    public Step(Integer id, int recipeId, int sequenceNumber, String title, String description, int durationMinutes) {
        this.id = id;
        this.recipeId = recipeId;
        this.sequenceNumber = sequenceNumber;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
    }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public Step(int recipeId, int sequenceNumber, String title, String description, int durationMinutes) {
        this(null, recipeId, sequenceNumber, title, description, durationMinutes);
    }

    public Integer getId() { return id; }
    public int getRecipeId() { return recipeId; }
    public int getSequenceNumber() { return sequenceNumber; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getDurationMinutes() { return durationMinutes; }

    public void setId(Integer id) { this.id = id; }

    @Override
    public String toString() {
        return "Step{" +
                "id=" + id +
                ", recipeId=" + recipeId +
                ", sequenceNumber=" + sequenceNumber +
                ", title='" + title + '\'' +
                ", durationMinutes=" + durationMinutes +
                '}';
    }
}
