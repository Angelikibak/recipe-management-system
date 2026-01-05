package recipes.ui;

import recipes.database.StepRepository;
import recipes.model.Step;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ManageStepsFrame extends JFrame {

    private final int recipeId;
    private final StepRepository stepRepo = new StepRepository();

    private final DefaultListModel<Step> listModel = new DefaultListModel<>();
    private final JList<Step> stepList = new JList<>(listModel);

    private final JSpinner seqSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JTextField titleField = new JTextField();
    private final JTextArea descArea = new JTextArea(5, 20);
    private final JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));

    // ✅ Step photo UI state
    private final JLabel stepPhotoPreview = new JLabel("No photo");
    private String selectedStepPhotoPath = null;

    private JButton manageIngredientsBtn;

    private Step selectedStep = null;

    public ManageStepsFrame(int recipeId) {
        this.recipeId = recipeId;

        setTitle("Manage Steps (Recipe ID: " + recipeId + ")");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildUI();
        loadSteps();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        // LEFT - Steps list
        stepList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stepList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Step s) {
                    setText(s.getSequenceNumber() + ". " + s.getTitle() + " (" + s.getDurationMinutes() + " min)");
                }
                return this;
            }
        });

        stepList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedStep = stepList.getSelectedValue();
                fillForm(selectedStep);
            }
        });

        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.add(new JLabel("Steps"), BorderLayout.NORTH);
        left.add(new JScrollPane(stepList), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadSteps());
        left.add(refreshBtn, BorderLayout.SOUTH);

        root.add(left, BorderLayout.WEST);

        manageIngredientsBtn = new JButton("Add/Manage Ingredients");
        manageIngredientsBtn.setEnabled(false);
        manageIngredientsBtn.addActionListener(e -> openManageIngredients());

        // RIGHT - Form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Sequence
        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Sequence Number"), c);
        c.gridx = 1;
        form.add(seqSpinner, c);

        // Title
        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Title"), c);
        c.gridx = 1;
        titleField.setColumns(25);
        form.add(titleField, c);

        // Duration
        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Duration (min)"), c);
        c.gridx = 1;
        form.add(durationSpinner, c);

        // Photo
        c.gridx = 0; c.gridy++;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        form.add(new JLabel("Photo"), c);

        JPanel photoPanel = new JPanel(new BorderLayout(8, 8));

        JButton choosePhotoBtn = new JButton("Choose Photo...");
        choosePhotoBtn.addActionListener(e -> chooseStepPhoto());

        stepPhotoPreview.setPreferredSize(new Dimension(240, 160));
        stepPhotoPreview.setHorizontalAlignment(SwingConstants.CENTER);
        stepPhotoPreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        photoPanel.add(choosePhotoBtn, BorderLayout.NORTH);
        photoPanel.add(stepPhotoPreview, BorderLayout.CENTER);

        c.gridx = 1;
        c.fill = GridBagConstraints.BOTH;
        form.add(photoPanel, c);

        // Description
        c.gridx = 0; c.gridy++;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        form.add(new JLabel("Description"), c);

        c.gridx = 1;
        c.fill = GridBagConstraints.BOTH;

        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(true);
        form.add(new JScrollPane(descArea), c);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton newBtn = new JButton("New");
        newBtn.addActionListener(e -> {
            selectedStep = null;
            clearForm();
            stepList.clearSelection();
        });

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> saveStep());

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> deleteStep());

        buttons.add(newBtn);
        buttons.add(saveBtn);
        buttons.add(deleteBtn);
        buttons.add(manageIngredientsBtn);

        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.add(new JLabel("Step Details"), BorderLayout.NORTH);
        right.add(form, BorderLayout.CENTER);
        right.add(buttons, BorderLayout.SOUTH);

        root.add(right, BorderLayout.CENTER);
    }

    private void loadSteps() {
        try {
            listModel.clear();
            List<Step> steps = stepRepo.findByRecipeId(recipeId);
            for (Step s : steps) listModel.addElement(s);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void fillForm(Step s) {
        if (s == null) return;

        seqSpinner.setValue(s.getSequenceNumber());
        titleField.setText(s.getTitle());
        descArea.setText(s.getDescription() == null ? "" : s.getDescription());
        durationSpinner.setValue(s.getDurationMinutes());

        // ✅ Photo
        selectedStepPhotoPath = s.getPhotoPath();
        setPreviewImage(stepPhotoPreview, selectedStepPhotoPath);
        manageIngredientsBtn.setEnabled(s != null && s.getId() != null && s.getId() > 0);
    }

    private void clearForm() {
        seqSpinner.setValue(1);
        titleField.setText("");
        descArea.setText("");
        durationSpinner.setValue(1);

        selectedStepPhotoPath = null;
        setPreviewImage(stepPhotoPreview, null);
        if (manageIngredientsBtn != null) manageIngredientsBtn.setEnabled(false);

    }

    private void saveStep() {
        try {
            int seq = (int) seqSpinner.getValue();
            String title = titleField.getText().trim();
            String desc = descArea.getText().trim();
            int duration = (int) durationSpinner.getValue();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title is required.");
                return;
            }

            if (selectedStep == null) {
                Step newStep = new Step(recipeId, seq, title, desc, duration);
                newStep.setPhotoPath(selectedStepPhotoPath);
                stepRepo.save(newStep);
            } else {
                Step updated = new Step(selectedStep.getId(), recipeId, seq, title, desc, duration);
                updated.setPhotoPath(selectedStepPhotoPath);
                stepRepo.update(updated);
            }

            loadSteps();
            clearForm();
            selectedStep = null;
            stepList.clearSelection();

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void deleteStep() {
        try {
            Step s = stepList.getSelectedValue();
            if (s == null) {
                JOptionPane.showMessageDialog(this, "Select a step first.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete step '" + s.getTitle() + "'?", "Confirm delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                stepRepo.deleteById(s.getId());
                loadSteps();
                clearForm();
                selectedStep = null;
                stepList.clearSelection();
            }

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void chooseStepPhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select step photo");

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            selectedStepPhotoPath = path;
            setPreviewImage(stepPhotoPreview, path);
        }
    }

    private void setPreviewImage(JLabel label, String path) {
        if (path == null || path.isBlank()) {
            label.setIcon(null);
            label.setText("No photo");
            return;
        }

        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(240, 160, Image.SCALE_SMOOTH);
        label.setText("");
        label.setIcon(new ImageIcon(img));
    }

    private void openManageIngredients() {
        Step s = stepList.getSelectedValue();
        if (s == null || s.getId() == null) {
            JOptionPane.showMessageDialog(this, "Select a saved step first.");
            return;
        }
        ManageStepIngredientsFrame frame = new ManageStepIngredientsFrame(s.getId());
        frame.setVisible(true);
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
