/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignmentgui;

import javax.swing.*;

/**
 *
 * @author dbson
 */
public class DifficultyPanel extends JPanel {
//    private JLabel nameLabel;
//    private JLabel passLabel;
//    
//    private JTextField username;
//    private JTextField password;
    
    private JButton easyButton;
    private JButton mediumButton;
    private JButton hardButton;
    
    
    public DifficultyPanel() {
        this.easyButton = new JButton("Easy");
        this.mediumButton = new JButton("Medium");
        this.hardButton = new JButton("Hard");
        
        this.add(this.easyButton);
        this.add(this.mediumButton);
        this.add(this.hardButton);
    }

    public JButton getEasyButton() {
        return easyButton;
    }

    public JButton getMediumButton() {
        return mediumButton;
    }

    public JButton getHardButton() {
        return hardButton;
    }
    
    
}
