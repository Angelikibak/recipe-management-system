package recipes.ui;

import recipes.model.ExecutionSession;
import recipes.model.Step;

import javax.swing.*;
import java.awt.*;

public class ExecuteRecipeFrame extends JFrame {

    private final ExecutionSession session;

    private final JLabel recipeLabel = new JLabel();
    private final JLabel stepTitleLabel = new JLabel();
    private final JTextArea stepDescriptionArea = new JTextArea();
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JButton completedButton = new JButton("Ολοκληρώθηκε");

    public ExecuteRecipeFrame(ExecutionSession session) {
        this.session = session;

        setTitle("Execute Recipe");
        setSize(600, 350);
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

        // Center: Step info
        JPanel center = new JPanel(new BorderLayout(8, 8));

        stepTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        center.add(stepTitleLabel, BorderLayout.NORTH);

        stepDescriptionArea.setLineWrap(true);
        stepDescriptionArea.setWrapStyleWord(true);
        stepDescriptionArea.setEditable(false);
        stepDescriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

        center.add(new JScrollPane(stepDescriptionArea), BorderLayout.CENTER);

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
            progressBar.setValue(100);
            completedButton.setEnabled(false);
            return;
        }

        stepTitleLabel.setText("Step " + current.getSequenceNumber() + ": " + current.getTitle());
        stepDescriptionArea.setText(current.getDescription() + "\n\nΔιάρκεια: " + current.getDurationMinutes() + " λεπτά");

        progressBar.setValue(session.getProgressPercent());
    }
}
