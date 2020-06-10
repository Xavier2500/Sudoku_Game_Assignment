/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignmentgui;

import javax.swing.JButton;

/**
 *
 * @author dbson
 */
public class DifficultyButton extends JButton {
    private char difficultyType;
    
    public DifficultyButton(String buttonName, char difficultyType) {
        super(buttonName);
        this.difficultyType = difficultyType;
    }
    
    public char getDifficultyType() {
        return this.difficultyType;
    }
}
