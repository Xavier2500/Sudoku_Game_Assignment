package globalview;

import extrabuttons.DifficultyButton;
import extrabuttons.HardButton;
import extrabuttons.MediumButton;
import extrabuttons.EasyButton;
import databaseconnectivity.SudokuDatabaseModel;
import java.awt.GridLayout;
import javax.swing.*;

/**
 * Holds all components to help users navigate through game set up. Holds things
 * needed for username and password input, as well as difficulty level selection.
 * Also holds the completed grids reset panel, for when the user has finished all
 * grids in a given difficulty level.
 * 
 * @author dbson
 */
public class DifficultyPanel extends JPanel {
    private JLabel nameLabel;
    private JLabel passwordLabel;
    private JLabel confirmLabel;
    
    private JTextField username;
    private JTextField password;
    
    private JButton confirmUser;
    private DifficultyButton[] difficultyOptions;
    
    private JPanel infoPanel;
    private JPanel confirmationPanel;
    private JPanel difficultyButtons;
    
    private DatabaseResetPanel resetCompletionPanel;
    
    /**
     * Sets up the layout for the programs starting panel with the required
     * components to ask user for input and grid difficulty.
     */
    public DifficultyPanel() {
        this.setLayout(null);
        
        this.infoPanel = new JPanel();
        this.infoPanel.setLayout(new GridLayout(2, 2));
        this.infoPanel.setSize(500, 50);
        this.infoPanel.setLocation(0, 0);
        this.infoPanel.setVisible(true);
        
        this.nameLabel = new JLabel("Username (20 Character Max): ");
        this.passwordLabel = new JLabel("Password (16 Character Max): ");
        
        this.username = new JTextField();
        this.password = new JTextField();
        
        this.infoPanel.add(this.nameLabel);
        this.infoPanel.add(this.username);
        this.infoPanel.add(this.passwordLabel);
        this.infoPanel.add(this.password);
        
        this.confirmationPanel = new JPanel();
        this.confirmationPanel.setLayout(new GridLayout(0, 2));
        this.confirmationPanel.setSize(500, 40);
        this.confirmationPanel.setLocation(0, 60);
        this.confirmationPanel.setVisible(true);
        
        this.confirmLabel = new JLabel("Please confirm entered information:");
        this.confirmUser = new JButton("Confirm User");
        
        this.confirmationPanel.add(this.confirmLabel);
        this.confirmationPanel.add(this.confirmUser);
        
        this.difficultyButtons = new JPanel();
        this.difficultyButtons.setLayout(new GridLayout(0, 3, 50, 0));
        this.difficultyButtons.setSize(500, 40);
        this.difficultyButtons.setLocation(0, 150);
        this.difficultyButtons.setVisible(true);
        
        this.difficultyOptions = new DifficultyButton[3];
        this.difficultyOptions[0] = new EasyButton();
        this.difficultyOptions[1] = new MediumButton();
        this.difficultyOptions[2] = new HardButton();
        
        for (DifficultyButton currentDifficulty : difficultyOptions) {
            this.difficultyButtons.add(currentDifficulty);
        }
        this.difficultyButtonAccessibility(false);
        
        this.resetCompletionPanel = new DatabaseResetPanel();
        this.resetCompletionPanel.setSize(500, 150);
        this.resetCompletionPanel.setLocation(0, 220);
        this.resetCompletionPanel.setVisible(false);
        
        this.add(this.infoPanel);
        this.add(this.confirmationPanel);
        this.add(this.difficultyButtons);
        this.add(this.resetCompletionPanel);
    }
    
    /**
     * Removes any information typed in the username and password text fields.
     */
    public void resetInputFields() {
        this.username.setText("");
        this.password.setText("");
    }
    
    /**
     * Changes the user confirmation button between active states.
     * 
     * @param isAccessible Whether the button is active.
     */
    public void confirmDataAccessibility(boolean isAccessible) {
        this.confirmUser.setEnabled(isAccessible);
    }
    
    /**
     * Changes the difficulty buttons between active states.
     * 
     * @param isAccessible Whether the buttons are active.
     */
    public void difficultyButtonAccessibility(boolean isAccessible) {
        for (DifficultyButton currentDifficulty : difficultyOptions) {
            currentDifficulty.setEnabled(isAccessible);
        }
    }
    
    /**
     * Show the database reset panel.
     * 
     * @param isViewable Controls whether the reset panel is viewable, and if
     * the difficulty buttons are accessible.
     */
    public void showDatabaseReset(boolean isViewable) {
        boolean accessDifficulty = !isViewable;
        
        this.resetCompletionPanel.setVisible(isViewable);
        this.difficultyButtonAccessibility(accessDifficulty);
    }
    
    /**
     * Checks the stored database to see if the information entered corresponds
     * with a user already created in the system. If it does, the system returns
     * the user for future use, and gives a confirm message.
     * 
     * @param sudokuDB Reference to the database connection.
     * @return User with the name and password given. Returns null if either
     * field is empty, or the password for a user was incorrect.
     */
    public User checkUserInfo(SudokuDatabaseModel sudokuDB) {
        String usernameInput = this.username.getText();
        String passwordInput = this.password.getText();
        
        User currentUser = null;
        String replyText = "";
        
        if (!usernameInput.isEmpty() || !passwordInput.isEmpty()) {
            currentUser = sudokuDB.checkUser(usernameInput, passwordInput);
        }
        
        if (currentUser != null) {
            replyText = ("Welcome, " + currentUser.getUsername());
            this.confirmDataAccessibility(false);
            this.difficultyButtonAccessibility(true);
        } else if (usernameInput.isEmpty() || passwordInput.isEmpty()){
            replyText = "Please provide information in both fields:";
        } else {
            replyText = "Password is incorrect. Please try again:";
        }
        
        this.confirmLabel.setText(replyText);
        
        return currentUser;
    }

    public JButton getConfirmUser() {
        return confirmUser;
    }
    
    public DifficultyButton getChooseDifficulty(int buttonIndex) {
        return difficultyOptions[buttonIndex];
    }

    public DifficultyButton[] getDifficultyOptions() {
        return difficultyOptions;
    }

    public JTextField getUsername() {
        return username;
    }

    public JTextField getPassword() {
        return password;
    }

    public DatabaseResetPanel getResetCompletionPanel() {
        return resetCompletionPanel;
    }
}
