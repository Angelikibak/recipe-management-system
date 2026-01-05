package recipes.ui;

import recipes.database.IngredientRepository;
import recipes.database.StepIngredientRepository;
import recipes.model.Ingredient;
import recipes.model.StepIngredient;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ManageStepIngredientsFrame extends JFrame {

    private final int stepId;

    private final IngredientRepository ingredientRepo = new IngredientRepository();
    private final StepIngredientRepository stepIngredientRepo = new StepIngredientRepository();

    private final DefaultListModel<StepIngredient> listModel = new DefaultListModel<>();
    private final JList<StepIngredient> ingredientList = new JList<>(listModel);

    private final JTextField ingredientNameField = new JTextField();
    private final JTextField quantityField = new JTextField();
    private final JTextField unitField = new JTextField();
    private final JTextField noteField = new JTextField();

    private StepIngredient selected = null;

    public ManageStepIngredientsFrame(int stepId) {
        this.stepId = stepId;

        setTitle("Manage Ingredients (Step ID: " + stepId + ")");
        setSize(800, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildUI();
        loadList();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        // LEFT list
        ingredientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ingredientList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selected = ingredientList.getSelectedValue();
                fillForm(selected);
            }
        });

        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.add(new JLabel("Ingredients for this step"), BorderLayout.NORTH);
        left.add(new JScrollPane(ingredientList), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadList());
        left.add(refreshBtn, BorderLayout.SOUTH);

        root.add(left, BorderLayout.WEST);

        // RIGHT form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        form.add(new JLabel("Ingredient name"), c);
        c.gridx = 1;
        ingredientNameField.setColumns(25);
        form.add(ingredientNameField, c);

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Quantity"), c);
        c.gridx = 1;
        form.add(quantityField, c);

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Unit"), c);
        c.gridx = 1;
        form.add(unitField, c);

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Note"), c);
        c.gridx = 1;
        form.add(noteField, c);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton newBtn = new JButton("New");
        newBtn.addActionListener(e -> {
            selected = null;
            ingredientList.clearSelection();
            clearForm();
        });

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> save());

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> delete());

        buttons.add(newBtn);
        buttons.add(saveBtn);
        buttons.add(deleteBtn);

        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.add(new JLabel("Ingredient details"), BorderLayout.NORTH);
        right.add(form, BorderLayout.CENTER);
        right.add(buttons, BorderLayout.SOUTH);

        root.add(right, BorderLayout.CENTER);
    }

    private void loadList() {
        try {
            listModel.clear();
            List<StepIngredient> items = stepIngredientRepo.findByStepId(stepId);
            for (StepIngredient si : items) listModel.addElement(si);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void fillForm(StepIngredient si) {
        if (si == null) return;

        ingredientNameField.setText(si.getIngredientName());
        quantityField.setText(si.getQuantity() == null ? "" : String.valueOf(si.getQuantity()));
        unitField.setText(si.getUnit() == null ? "" : si.getUnit());
        noteField.setText(si.getNote() == null ? "" : si.getNote());
    }

    private void clearForm() {
        ingredientNameField.setText("");
        quantityField.setText("");
        unitField.setText("");
        noteField.setText("");
    }

    private void save() {
        try {
            String ingName = ingredientNameField.getText().trim();
            if (ingName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingredient name is required.");
                return;
            }

            Double qty = null;
            String qtyRaw = quantityField.getText().trim();
            if (!qtyRaw.isEmpty()) {
                try {
                    qty = Double.parseDouble(qtyRaw);
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this, "Quantity must be a number.");
                    return;
                }
            }

            String unit = unitField.getText().trim();
            if (unit.isEmpty()) unit = null;

            String note = noteField.getText().trim();
            if (note.isEmpty()) note = null;

            // 1) get or create ingredient
            Ingredient ing = ingredientRepo.getOrCreateByName(ingName);

            // 2) upsert relationship
            stepIngredientRepo.upsert(stepId, ing.getId(), qty, unit, note);

            loadList();
            clearForm();
            selected = null;
            ingredientList.clearSelection();

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void delete() {
        try {
            StepIngredient si = ingredientList.getSelectedValue();
            if (si == null) {
                JOptionPane.showMessageDialog(this, "Select an ingredient first.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Remove ingredient '" + si.getIngredientName() + "' from this step?",
                    "Confirm delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                stepIngredientRepo.delete(stepId, si.getIngredientId());
                loadList();
                clearForm();
                selected = null;
                ingredientList.clearSelection();
            }

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
