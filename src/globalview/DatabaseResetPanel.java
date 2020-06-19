package globalview;

import extrabuttons.ChangeDifficultyButton;
import java.awt.GridLayout;
import javax.swing.*;

/**
 * Deals with a panel only used when a user has completed every sudoku grid in a
 * given difficulty level. Informs user that all grids of the requested difficulty
 * have been completed, and asks f the user would like to reset their progress,
 * or choose a different difficulty.
 * 
 * @author dbson
 */
public class DatabaseResetPanel extends JPanel {

    private JLabel allGridsCompleted;
    private JLabel resetLabel;
    private JLabel changeLabel;

    private JButton resetDatabaseButton;
    private JButton changeDifficultyButton;

    private JPanel showTextPanel;
    private JPanel showButtonsPanel;

    public DatabaseResetPanel() {
        this.allGridsCompleted = new JLabel("You have completed all grids of the chosen difficulty.");
        this.allGridsCompleted.setHorizontalAlignment(SwingConstants.CENTER);
        
        this.resetLabel = new JLabel("Would you like to delete your grid "
                + "completions of you selected difficulty and reset?");
        this.resetLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        this.changeLabel = new JLabel("Or, would you like to change difficulties?");
        this.changeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        this.showTextPanel = new JPanel();
        this.showTextPanel.setLayout(new GridLayout(0, 1, 0, 10));
        this.showTextPanel.add(this.allGridsCompleted);
        this.showTextPanel.add(this.resetLabel);
        this.showTextPanel.add(this.changeLabel);

        this.resetDatabaseButton = new JButton("Reset Completion");
        this.changeDifficultyButton = new ChangeDifficultyButton();

        this.showButtonsPanel = new JPanel();
        this.showButtonsPanel.setLayout(new GridLayout(0, 2, 30, 0));
        this.showButtonsPanel.add(this.resetDatabaseButton);
        this.showButtonsPanel.add(this.changeDifficultyButton);

        this.setLayout(new GridLayout(0, 1, 0, 10));
        this.add(this.showTextPanel);
        this.add(this.showButtonsPanel);
    }

    public JButton getResetDatabase() {
        return resetDatabaseButton;
    }

    public JButton getChangeDifficulty() {
        return changeDifficultyButton;
    }
}
