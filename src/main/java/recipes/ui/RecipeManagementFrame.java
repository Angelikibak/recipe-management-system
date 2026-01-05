package recipes.ui;

import recipes.database.CategoryRepository;
import recipes.database.RecipeRepository;
import recipes.model.Category;
import recipes.model.ExecutionSession;
import recipes.model.Recipe;
import recipes.service.ExecutionService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RecipeManagementFrame extends JFrame {

    private final RecipeRepository recipeRepo = new RecipeRepository();
    private final ExecutionService executionService = new ExecutionService();
    private final CategoryRepository categoryRepo = new CategoryRepository();

    private final DefaultListModel<Recipe> listModel = new DefaultListModel<>();
    private final JList<Recipe> recipeList = new JList<>(listModel);

    private final JTextField nameField = new JTextField();
    private final JTextArea descriptionArea = new JTextArea(5, 20);
    private final JComboBox<Category> categoryBox = new JComboBox<>();
    private final JComboBox<String> difficultyBox = new JComboBox<>(new String[]{"EASY", "MEDIUM", "HARD"});
    private final JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 1000, 1));

    // ✅ Photo UI state
    private final JLabel recipePhotoPreview = new JLabel("No photo");
    private String selectedRecipePhotoPath = null;

    private Recipe selectedRecipe = null;

    private JButton manageStepsBtn;
    private JButton manageRecipeIngredientsBtn;

    public RecipeManagementFrame() {
        setTitle("Recipe Management System");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buildUI();

        // ✅ Πρώτα categories, μετά recipes (για να μπορεί fillForm να κάνει select category)
        loadCategories();
        loadRecipes();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        // LEFT: list
        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recipeList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Recipe r) {
                    setText(r.getId() + " - " + r.getName() + " (" + r.getDifficulty() + ")");
                }
                return this;
            }
        });

        recipeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedRecipe = recipeList.getSelectedValue();
                fillForm(selectedRecipe);
                updateStepsButtonState();
            }
        });

        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.add(new JLabel("Recipes"), BorderLayout.NORTH);
        left.add(new JScrollPane(recipeList), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            loadCategories();
            loadRecipes();
        });
        left.add(refreshBtn, BorderLayout.SOUTH);

        root.add(left, BorderLayout.WEST);

        // RIGHT: form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        // Name
        form.add(new JLabel("Name"), c);
        c.gridx = 1;
        form.add(nameField, c);

        // Category
        c.gridx = 0;
        c.gridy++;
        form.add(new JLabel("Category"), c);
        c.gridx = 1;
        form.add(categoryBox, c);

        // Difficulty
        c.gridx = 0;
        c.gridy++;
        form.add(new JLabel("Difficulty"), c);
        c.gridx = 1;
        form.add(difficultyBox, c);

        // Duration
        c.gridx = 0;
        c.gridy++;
        form.add(new JLabel("Total Duration (min)"), c);
        c.gridx = 1;
        form.add(durationSpinner, c);

        // Photo row
        c.gridx = 0;
        c.gridy++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Photo"), c);

        JPanel photoPanel = new JPanel(new BorderLayout(8, 8));

        JButton choosePhotoBtn = new JButton("Choose Photo...");
        choosePhotoBtn.addActionListener(e -> chooseRecipePhoto());

        recipePhotoPreview.setPreferredSize(new Dimension(220, 150));
        recipePhotoPreview.setHorizontalAlignment(SwingConstants.CENTER);
        recipePhotoPreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        photoPanel.add(choosePhotoBtn, BorderLayout.NORTH);
        photoPanel.add(recipePhotoPreview, BorderLayout.CENTER);

        c.gridx = 1;
        c.fill = GridBagConstraints.BOTH;
        form.add(photoPanel, c);

        // Description
        c.gridx = 0;
        c.gridy++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Description"), c);

        c.gridx = 1;
        c.fill = GridBagConstraints.BOTH;

        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        form.add(new JScrollPane(descriptionArea), c);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton newBtn = new JButton("New");
        newBtn.addActionListener(e -> {
            selectedRecipe = null;
            recipeList.clearSelection();
            clearForm();               // ✅ καθαρίζει fields + photo path
            updateStepsButtonState();  // ✅ απενεργοποιεί σωστά τα κουμπιά
        });

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> saveRecipe());

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> deleteRecipe());

        JButton executeBtn = new JButton("Execute");
        executeBtn.addActionListener(e -> executeSelectedRecipe());

        manageStepsBtn = new JButton("Add/Manage Steps");
        manageStepsBtn.setEnabled(false);
        manageStepsBtn.addActionListener(e -> openManageSteps());

        manageRecipeIngredientsBtn = new JButton("Add/Manage Recipe Ingredients");
        manageRecipeIngredientsBtn.setEnabled(false);
        manageRecipeIngredientsBtn.addActionListener(e -> openManageRecipeIngredients());

        buttons.add(newBtn);
        buttons.add(saveBtn);
        buttons.add(deleteBtn);
        buttons.add(executeBtn);
        buttons.add(manageStepsBtn);
        buttons.add(manageRecipeIngredientsBtn);

        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.add(new JLabel("Recipe Details"), BorderLayout.NORTH);
        right.add(form, BorderLayout.CENTER);
        right.add(buttons, BorderLayout.SOUTH);

        root.add(right, BorderLayout.CENTER);
    }

    private void loadCategories() {
        try {
            categoryBox.removeAllItems();
            categoryBox.addItem(new Category(null, "— Select —"));

            for (Category cat : categoryRepo.findAll()) {
                categoryBox.addItem(cat);
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadRecipes() {
        try {
            listModel.clear();
            List<Recipe> recipes = recipeRepo.findAll();
            for (Recipe r : recipes) {
                listModel.addElement(r);
            }
            updateStepsButtonState();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void fillForm(Recipe r) {
        if (r == null) return;

        nameField.setText(r.getName());
        descriptionArea.setText(r.getDescription() == null ? "" : r.getDescription());
        difficultyBox.setSelectedItem(r.getDifficulty());
        durationSpinner.setValue(r.getTotalDurationMinutes());

        // ✅ Select category by id
        Integer catId = r.getCategoryId();
        boolean found = false;

        for (int i = 0; i < categoryBox.getItemCount(); i++) {
            Category item = categoryBox.getItemAt(i);
            if (item != null && item.getId() != null && item.getId().equals(catId)) {
                categoryBox.setSelectedIndex(i);
                found = true;
                break;
            }
        }
        if (!found) categoryBox.setSelectedIndex(0);

        // ✅ Photo
        selectedRecipePhotoPath = r.getPhotoPath();
        setPreviewImage(recipePhotoPreview, selectedRecipePhotoPath);
    }

    private void clearForm() {
        nameField.setText("");
        descriptionArea.setText("");
        difficultyBox.setSelectedItem("EASY");
        durationSpinner.setValue(20);
        categoryBox.setSelectedIndex(0);

        // ✅ reset photo state (ΣΗΜΑΝΤΙΚΟ)
        selectedRecipePhotoPath = null;
        setPreviewImage(recipePhotoPreview, null);
    }

    private void selectRecipeInListById(int id) {
        for (int i = 0; i < listModel.size(); i++) {
            Recipe r = listModel.getElementAt(i);
            if (r.getId() != null && r.getId().intValue() == id) {
                recipeList.setSelectedIndex(i);
                recipeList.ensureIndexIsVisible(i);
                selectedRecipe = r;
                fillForm(r);
                return;
            }
        }
    }

    private void saveRecipe() {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name is required.");
                return;
            }

            String desc = descriptionArea.getText().trim();
            String diff = (String) difficultyBox.getSelectedItem();
            int duration = (int) durationSpinner.getValue();

            Category selectedCat = (Category) categoryBox.getSelectedItem();
            Integer categoryId = (selectedCat == null) ? null : selectedCat.getId();

            int idToSelect;

            if (selectedRecipe == null) {
                Recipe newRecipe = new Recipe(name, desc, diff, duration);
                newRecipe.setCategoryId(categoryId);
                newRecipe.setPhotoPath(selectedRecipePhotoPath);

                recipeRepo.save(newRecipe);
                idToSelect = newRecipe.getId().intValue();
            } else {
                Recipe updated = new Recipe(selectedRecipe.getId(), name, desc, diff, duration);
                updated.setCategoryId(categoryId);
                updated.setPhotoPath(selectedRecipePhotoPath);

                recipeRepo.update(updated);
                idToSelect = selectedRecipe.getId();
            }

            loadRecipes();
            selectRecipeInListById(idToSelect);
            updateStepsButtonState();

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void deleteRecipe() {
        try {
            Recipe r = recipeList.getSelectedValue();
            if (r == null) {
                JOptionPane.showMessageDialog(this, "Select a recipe first.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete recipe '" + r.getName() + "'?",
                    "Confirm delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                recipeRepo.deleteById(r.getId());
                loadRecipes();
                selectedRecipe = null;
                recipeList.clearSelection();
                clearForm();
                updateStepsButtonState();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void executeSelectedRecipe() {
        try {
            Recipe r = recipeList.getSelectedValue();
            if (r == null) {
                JOptionPane.showMessageDialog(this, "Select a recipe first.");
                return;
            }

            ExecutionSession session = executionService.startExecution(r.getId());
            ExecuteRecipeFrame frame = new ExecuteRecipeFrame(session);
            frame.setVisible(true);

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void updateStepsButtonState() {
        Recipe selected = recipeList.getSelectedValue();
        boolean enabled = selected != null && selected.getId() != null && selected.getId() > 0;

        manageStepsBtn.setEnabled(enabled);
        manageRecipeIngredientsBtn.setEnabled(enabled);
    }

    private void openManageSteps() {
        try {
            Recipe r = recipeList.getSelectedValue();
            if (r == null || r.getId() == null || r.getId() <= 0) {
                JOptionPane.showMessageDialog(this, "Save/select a recipe first, then add steps.");
                return;
            }
            ManageStepsFrame frame = new ManageStepsFrame(r.getId());
            frame.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void openManageRecipeIngredients() {
        Recipe r = recipeList.getSelectedValue();
        if (r == null || r.getId() == null || r.getId() <= 0) {
            JOptionPane.showMessageDialog(this, "Save/select a recipe first, then add ingredients.");
            return;
        }

        ManageRecipeIngredientsFrame frame = new ManageRecipeIngredientsFrame(r.getId());
        frame.setVisible(true);
    }

    private void chooseRecipePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select recipe photo");

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            selectedRecipePhotoPath = path;
            setPreviewImage(recipePhotoPreview, path);
        }
    }

    private void setPreviewImage(JLabel label, String path) {
        if (path == null || path.isBlank()) {
            label.setIcon(null);
            label.setText("No photo");
            return;
        }

        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(220, 150, Image.SCALE_SMOOTH);
        label.setText("");
        label.setIcon(new ImageIcon(img));
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}