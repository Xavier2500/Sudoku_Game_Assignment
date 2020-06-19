package globalview;

import extrabuttons.ChangeDifficultyButton;
import extrabuttons.ChangeUserButton;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Appears when a user has completed a grid. Allows the user to get a new grid,
 * change difficulty, change user, or leave the game.
 * 
 * @author dbson
 */
public class GridCompletedPanel extends JPanel {
    private JPanel textPanel;
    private JPanel buttonPanel;
    
    private JLabel completedText;
    private JLabel userScoreLabel;
    
    private JButton changeUserButton;
    private JButton changeDifficultyButton;
    private JButton leaveButton;
    
    public GridCompletedPanel() {
        this.setLayout(null);
        
        this.completedText = new JLabel("You have completed the grid! Good Job!");
        this.completedText.setFont(new Font("Arial", Font.PLAIN, 20));
        this.completedText.setHorizontalAlignment(SwingConstants.CENTER);
        
        this.userScoreLabel = new JLabel();
        this.userScoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        this.userScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        this.textPanel = new JPanel();
        this.textPanel.setLayout(new GridLayout(0, 1));
        this.textPanel.setSize(450, 70);
        this.textPanel.setLocation(0, 0);
        this.textPanel.setVisible(true);
        
        this.textPanel.add(this.completedText);
        this.textPanel.add(this.userScoreLabel);
        
        this.changeUserButton = new ChangeUserButton();
        this.changeDifficultyButton = new ChangeDifficultyButton();
        this.leaveButton = new JButton("Leave Game");
        
        this.buttonPanel = new JPanel();
        this.buttonPanel.setLayout(new GridLayout(0, 3, 20, 0));
        this.buttonPanel.setSize(450, 50);
        this.buttonPanel.setLocation(0, 80);
        this.buttonPanel.setVisible(true);
        
        this.buttonPanel.add(this.changeUserButton);
        this.buttonPanel.add(this.changeDifficultyButton);
        this.buttonPanel.add(this.leaveButton);
        
        this.add(this.textPanel);
        this.add(this.buttonPanel);
    }
    
    /**
     * Show the current user's score after completing a new grid.
     * 
     * @param currentUser Player currently using the program.
     */
    public void updateUserScoreView(User currentUser) {
        String scoreText = ("You now have a total of "
                + currentUser.getScore() + " points. Well done!");
        this.userScoreLabel.setText(scoreText);
    }
    
    public JButton getChangeUserButton() {
        return changeUserButton;
    }

    public JButton getChangeDifficultyButton() {
        return changeDifficultyButton;
    }

    public JButton getLeaveButton() {
        return leaveButton;
    }
}
