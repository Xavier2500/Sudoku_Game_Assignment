/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignmentgui;

import java.awt.GridLayout;
import javax.swing.*;

/**
 *
 * @author dbson
 */
public class DifficultyPanel extends JPanel {
    private JLabel nameLabel;
    private JLabel passwordLabel;
    private JLabel confirmLabel;
    private JLabel difficultyLabel;
    
    private JTextField username;
    private JTextField password;
    
    private JButton confirmUser;
    private DifficultyButton[] chooseDifficulty;
    
    private JPanel infoPanel;
    private JPanel confirmationPanel;
    private JPanel difficultyButtons;
    
    /**
     * Sets up the layout for the programs starting panel with the required
     * components to ask user for input and grid difficulty.
     */
    public DifficultyPanel() {
        this.setLayout(new GridLayout(0, 1));
        
        this.infoPanel = new JPanel();
        this.infoPanel.setLayout(new GridLayout(2, 2));
        
        this.nameLabel = new JLabel("Usernmae: ");
        this.passwordLabel = new JLabel("Password: ");
        
        this.username = new JTextField();
        this.password = new JTextField();
        
        this.infoPanel.add(this.nameLabel);
        this.infoPanel.add(this.username);
        this.infoPanel.add(this.passwordLabel);
        this.infoPanel.add(this.password);
        
        this.confirmationPanel = new JPanel();
        this.confirmationPanel.setLayout(new GridLayout(2, 0));
        
        this.confirmLabel = new JLabel("Please confirm entered information.");
        this.confirmUser = new JButton("Confirm User");
        
        this.confirmationPanel.add(this.confirmLabel);
        this.confirmationPanel.add(this.confirmUser);
        
        this.difficultyButtons = new JPanel();
        
        this.difficultyLabel = new JLabel("Please choose your difficulty.");
        
        this.chooseDifficulty = new DifficultyButton[3];
        this.chooseDifficulty[0] = new EasyButton();
        this.chooseDifficulty[1] = new MediumButton();
        this.chooseDifficulty[2] = new HardButton();
        
        for (DifficultyButton currentDifficulty : chooseDifficulty) {
            this.difficultyButtons.add(currentDifficulty);
        }
        this.difficultyPanelAccessibility(false);
        
        this.add(this.infoPanel);
        this.add(this.confirmationPanel);
        this.add(this.difficultyButtons);
    }
    
    public void confirmDataAccessibility(boolean isAccessible) {
        this.confirmUser.setEnabled(isAccessible);
    }
    
    public void difficultyPanelAccessibility(boolean isAccessible) {
        for (DifficultyButton currentDifficulty : chooseDifficulty) {
            currentDifficulty.setEnabled(isAccessible);
        }
    }
    
    /**
     * Checks the stored database to see if the information entered corresponds
     * with a user already created in the system. If it does, the system returns
     * the user for future use, and gives a confirm message. If the user does not
     * exist, an error message is given.
     * 
     * @param sudokuDB
     * @return 
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
            this.difficultyPanelAccessibility(true);
        } else {
            replyText = "Information has not been fully given. "
                    + "Please provide text in both fields.";
        }
        
        this.confirmLabel.setText(replyText);
        
        return currentUser;
    }

    public JButton getConfirmUser() {
        return confirmUser;
    }
    
    public DifficultyButton getChooseDifficulty(int buttonIndex) {
        return chooseDifficulty[buttonIndex];
    }

    public DifficultyButton[] getChooseDifficulty() {
        return chooseDifficulty;
    }
}
