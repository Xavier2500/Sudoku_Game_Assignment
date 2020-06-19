/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extrabuttons;

import javax.swing.JButton;

/**
 * Designed to make the actionPerformed method in the GlobalController class
 * less cluttered, by allowing for the use of a 'instanceof' to simplify
 * button presses for choosing difficulty.
 * 
 * @author dbson
 */
public class DifficultyButton extends JButton {
    private char difficultyType;
    
    public DifficultyButton(String buttonName, char difficultyType) {
        super(buttonName);
        this.difficultyType = difficultyType;
    }
    
    /**
     * Get the character representing the difficulty type of the button.
     * 
     * @return Character representing the difficulty type given by the button.
     */
    public char getDifficultyType() {
        return this.difficultyType;
    }
}
