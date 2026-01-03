package recipes.ui;

import recipes.database.RecipeRepository;
import recipes.model.ExecutionSession;
import recipes.model.Recipe;
import recipes.service.ExecutionService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RecipeManagementFrame extends JFrame {

    private final RecipeRepository recipeRepo = new RecipeRepository();
    private final ExecutionService executionService = new ExecutionService();

    private final DefaultListModel<Recipe> listModel = new DefaultListModel<>();
    private final JList<Recipe> recipeList = new JList<>(listModel);

    private final JTextField nameField = new JTextField();
    private final JTextArea descriptionArea = new JTextArea(5, 20);
    private final JComboBox<String> difficultyBox = new JComboBox<>(new String[]{"EASY", "MEDIUM", "HARD"});
    private final JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 1000, 1));

    private Recipe selectedRecipe = null;

    private JButton manageStepsBtn;

    public RecipeManagementFrame() {
        setTitle("Recipe Management System");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buildUI();
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
        refreshBtn.addActionListener(e -> loadRecipes());
        left.add(refreshBtn, BorderLayout.SOUTH);

        root.add(left, BorderLayout.WEST);

        // RIGHT: form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        form.add(new JLabel("Name"), c);
        c.gridx = 1;
        form.add(nameField, c);

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Difficulty"), c);
        c.gridx = 1;
        form.add(difficultyBox, c);

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Total Duration (min)"), c);
        c.gridx = 1;
        form.add(durationSpinner, c);

        c.gridx = 0; c.gridy++;
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
            clearForm();
            recipeList.clearSelection();
            updateStepsButtonState();
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

        buttons.add(newBtn);
        buttons.add(saveBtn);
        buttons.add(deleteBtn);
        buttons.add(executeBtn);
        buttons.add(manageStepsBtn);

        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.add(new JLabel("Recipe Details"), BorderLayout.NORTH);
        right.add(form, BorderLayout.CENTER);
        right.add(buttons, BorderLayout.SOUTH);

        root.add(right, BorderLayout.CENTER);
    }

    private void loadRecipes() {
        try {
            listModel.clear();
            List<Recipe> recipes = recipeRepo.findAll();
            for (Recipe r : recipes) listModel.addElement(r);

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
    }

    private void clearForm() {
        nameField.setText("");
        descriptionArea.setText("");
        difficultyBox.setSelectedItem("EASY");
        durationSpinner.setValue(20);
    }

    private void selectRecipeInListById(int id) {
        for (int i = 0; i < listModel.size(); i++) {
            Recipe r = listModel.getElementAt(i);
            if (r.getId() == id) {
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
            int idToSelect;

            if (selectedRecipe == null) {
                Recipe newRecipe = new Recipe(name, desc, diff, duration);
                recipeRepo.save(newRecipe);
                idToSelect = newRecipe.getId();
            } else {
                Recipe updated = new Recipe(selectedRecipe.getId(), name, desc, diff, duration);
                recipeRepo.update(updated);
                idToSelect= selectedRecipe.getId();
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

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete recipe '" + r.getName() + "'?", "Confirm delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                recipeRepo.deleteById(r.getId());
                loadRecipes();
                clearForm();
                selectedRecipe = null;
                recipeList.clearSelection();
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
        boolean enabled = selected != null && selected.getId() > 0;
        manageStepsBtn.setEnabled(enabled);
    }

    private void openManageSteps() {
        try {
            Recipe r = recipeList.getSelectedValue();
            if (r == null) {
                JOptionPane.showMessageDialog(this, "Select a recipe first.");
                return;
            }
            ManageStepsFrame frame = new ManageStepsFrame(r.getId());
            frame.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
