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
public class ExtrasPanel extends JPanel {
    private JButton userChange;
    private JButton diffChange;
    private JButton reset;
    private JButton hint;
    private JButton checkProg;
    
    public ExtrasPanel() {
        this.setLayout(new GridLayout(1, 0, 5, 0));
        
        this.userChange = new JButton("Change User");
        this.diffChange = new JButton("Change Difficulty");
        this.reset = new JButton("Reset");
        this.hint = new JButton("Hint");
        this.checkProg = new JButton("Check Completion");
        
        this.add(this.userChange);
        this.add(this.diffChange);
        this.add(this.reset);
        this.add(this.hint);
        this.add(this.checkProg);
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
    
    
}
