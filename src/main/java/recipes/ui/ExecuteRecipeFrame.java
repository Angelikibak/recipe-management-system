package recipes.ui;

import recipes.database.StepIngredientRepository;
import recipes.model.ExecutionSession;
import recipes.model.Step;
import recipes.model.StepIngredient;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ExecuteRecipeFrame extends JFrame {

    private final ExecutionSession session;

    private final StepIngredientRepository stepIngredientRepo = new StepIngredientRepository();

    private final JLabel recipeLabel = new JLabel();
    private final JLabel stepTitleLabel = new JLabel();

    private final JTextArea stepDescriptionArea = new JTextArea();

    // NEW: ingredients area
    private final JTextArea ingredientsArea = new JTextArea();

    // NEW: photo preview
    private final JLabel photoLabel = new JLabel("No photo", SwingConstants.CENTER);

    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JButton completedButton = new JButton("Next Step");

    public ExecuteRecipeFrame(ExecutionSession session) {
        this.session = session;

        setTitle("Execute Recipe");
        setSize(900, 500); // λίγο μεγαλύτερο για να χωράει photo + ingredients
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buildUI();
        refreshUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        // Top: Recipe name
        recipeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(recipeLabel, BorderLayout.NORTH);

        // Center: Step info (title + description + photo + ingredients)
        JPanel center = new JPanel(new BorderLayout(8, 8));

        stepTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        center.add(stepTitleLabel, BorderLayout.NORTH);

        // Description area
        stepDescriptionArea.setLineWrap(true);
        stepDescriptionArea.setWrapStyleWord(true);
        stepDescriptionArea.setEditable(false);
        stepDescriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JScrollPane descScroll = new JScrollPane(stepDescriptionArea);

        // Photo panel (right side)
        photoLabel.setPreferredSize(new Dimension(260, 260));
        photoLabel.setBorder(BorderFactory.createTitledBorder("Photo"));

        JPanel middle = new JPanel(new BorderLayout(8, 8));
        middle.add(descScroll, BorderLayout.CENTER);
        middle.add(photoLabel, BorderLayout.EAST);

        center.add(middle, BorderLayout.CENTER);

        // Ingredients area (bottom of center)
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);
        ingredientsArea.setEditable(false);
        ingredientsArea.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JScrollPane ingScroll = new JScrollPane(ingredientsArea);
        ingScroll.setBorder(BorderFactory.createTitledBorder("Υλικά βήματος"));
        ingScroll.setPreferredSize(new Dimension(200, 130));

        center.add(ingScroll, BorderLayout.SOUTH);

        root.add(center, BorderLayout.CENTER);

        // Bottom: Progress + button
        JPanel bottom = new JPanel(new BorderLayout(8, 8));

        progressBar.setStringPainted(true);
        bottom.add(progressBar, BorderLayout.CENTER);

        completedButton.addActionListener(e -> {
            session.markCurrentStepCompleted();
            refreshUI();
        });
        bottom.add(completedButton, BorderLayout.EAST);

        root.add(bottom, BorderLayout.SOUTH);
    }

    private void refreshUI() {
        recipeLabel.setText("Recipe: " + session.getRecipe().getName());

        Step current = session.getCurrentStep();
        if (current == null) {
            stepTitleLabel.setText("✅ Η συνταγή ολοκληρώθηκε!");
            stepDescriptionArea.setText("Τελικό progress: " + session.getProgressPercent() + "%");
            ingredientsArea.setText("");
            photoLabel.setIcon(null);
            photoLabel.setText("No photo");
            progressBar.setValue(100);
            completedButton.setEnabled(false);
            return;
        }

        stepTitleLabel.setText("Step " + current.getSequenceNumber() + ": " + current.getTitle());

        // Description + duration
        stepDescriptionArea.setText(
                current.getDescription()
                        + "\n\nΔιάρκεια: " + current.getDurationMinutes() + " λεπτά"
        );

        // Progress
        progressBar.setValue(session.getProgressPercent());

        // ====== NEW: Step Ingredients ======
        loadStepIngredients(current);

        // ====== NEW: Step Photo ======
        loadStepPhoto(current);
    }

    private void loadStepIngredients(Step step) {
        try {
            if (step.getId() == null) {
                ingredientsArea.setText("Δεν υπάρχουν υλικά (το βήμα δεν έχει αποθηκευμένο id).");
                return;
            }

            List<StepIngredient> items = stepIngredientRepo.findByStepId(step.getId());

            if (items.isEmpty()) {
                ingredientsArea.setText("Δεν έχουν οριστεί υλικά για αυτό το βήμα.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (StepIngredient si : items) {
                sb.append("• ").append(si.getIngredientName());

                // quantity + unit (αν υπάρχουν)
                if (si.getQuantity() != null) {
                    sb.append(" — ").append(si.getQuantity());
                    if (si.getUnit() != null && !si.getUnit().isBlank()) {
                        sb.append(" ").append(si.getUnit());
                    }
                }

                // note (αν υπάρχει)
                if (si.getNote() != null && !si.getNote().isBlank()) {
                    sb.append(" (").append(si.getNote()).append(")");
                }

                sb.append("\n");
            }

            ingredientsArea.setText(sb.toString());

        } catch (Exception ex) {
            ingredientsArea.setText("Σφάλμα φόρτωσης υλικών βήματος: " + ex.getMessage());
        }
    }

    private void loadStepPhoto(Step step) {
        try {
            String path = step.getPhotoPath();

            if (path == null || path.isBlank()) {
                photoLabel.setIcon(null);
                photoLabel.setText("No photo");
                return;
            }

            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage();

            // scale για να χωρέσει στο UI
            Image scaled = img.getScaledInstance(240, 240, Image.SCALE_SMOOTH);

            photoLabel.setText("");
            photoLabel.setIcon(new ImageIcon(scaled));

        } catch (Exception ex) {
            photoLabel.setIcon(null);
            photoLabel.setText("Photo load error");
        }
    }
}
