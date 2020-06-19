/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package globalview;

import extrabuttons.ChangeDifficultyButton;
import extrabuttons.ChangeUserButton;
import java.awt.GridLayout;
import javax.swing.*;

/**
 * Contains extra options allowed while playing a game. These extra controls
 * include: ability to go back and change the current user or difficulty level,
 * reseting the grid to a fresh state, getting a hint, checking current
 * completion progress and saving current completion of a grid.
 * 
 * @author dbson
 */
public class ExtrasPanel extends JPanel {
    private JButton userChange;
    private JButton diffChange;
    private JButton reset;
    private JButton hint;
    private JButton checkProg;
    private JButton save;
    
    private JLabel completion;
    private JLabel saveState;
    
    public ExtrasPanel() {
        this.setLayout(new GridLayout(0, 4, 5, 5));
        
        this.userChange = new ChangeUserButton();
        this.diffChange = new ChangeDifficultyButton();
        this.reset = new JButton("Reset");
        this.hint = new JButton("Hint");
        this.checkProg = new JButton("Check Completion");
        this.save = new JButton("Save");
        
        this.completion = new JLabel("Grid progress has not been checked.");
        this.saveState = new JLabel("No save has been made.");
        
        this.add(this.userChange);
        this.add(this.reset);
        this.add(this.checkProg);
        this.add(this.completion);
        
        this.add(this.diffChange);
        this.add(this.hint);
        this.add(this.save);
        this.add(this.saveState);
    }

    public JButton getUserChange() {
        return userChange;
    }

    public JButton getDiffChange() {
        return diffChange;
    }

    public JButton getReset() {
        return reset;
    }

    public JButton getCheckProg() {
        return checkProg;
    }

    public JButton getHint() {
        return hint;
    }

    public JButton getSave() {
        return save;
    }

    public JLabel getCompletion() {
        return completion;
    }

    public JLabel getSaveState() {
        return saveState;
    }
}
