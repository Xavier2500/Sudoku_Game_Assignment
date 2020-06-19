/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import globalview.GameControlPanel;
import extrabuttons.HintButton;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JTextField;
import listenerclasses.GameListener;

/**
 * Controls all actions that are done by the game itself. Covers the input of
 * numbers into cells, control button presses, and the interaction with the hint
 * panel.
 * 
 * @author dbson
 */
public class GameController extends GameListener {
    private GameControlPanel gameView;
    
    public GameController(GameControlPanel gameView) {
        this.gameView = gameView;
        
        JTextField[][] sudokuCells = this.gameView.getGrid().getCells();
        for (JTextField[] sudokuRow : sudokuCells) {
            for (JTextField sudokuCell : sudokuRow) {
                sudokuCell.addKeyListener(this);
            }
        }
        
        JButton[][] hintCells = this.gameView.getHint().getHintButtons();
        for (JButton[] hintRow : hintCells) {
            for (JButton hintCell : hintRow) {
                hintCell.addActionListener(this);
            }
        }
        
        this.gameView.getControls().getReset().addActionListener(this);
        this.gameView.getControls().getSave().addActionListener(this);
        this.gameView.getControls().getHint().addActionListener(this);
        this.gameView.getHint().getHintReturn().addActionListener(this);
    }
    
    private void hintButtonPressed(HintButton wasPressed) {
        int row = wasPressed.getRowNo();
        int column = wasPressed.getColumnNo();

        this.gameView.hintUsed(row, column);
    }
    
    /**
     * Whenever a character is entered, checks whether the text field already has
     * a character in it, and whether the character is a number. If either of
     * these conditions are correct, the event is consumed.
     * 
     * @param e Event processing the entering of numbers in the sudoku grid.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        JTextField fieldEntered = (JTextField) e.getComponent();
        int numChars = fieldEntered.getText().length();
        
        if ((numChars == 1) || !(Character.isDigit(e.getKeyChar()))) {
            e.consume();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        this.gameView.getGrid().updateModel();
        
        if (e.getSource() instanceof JButton) {
            JButton source = (JButton) e.getSource();
            
            if (source == this.gameView.getControls().getReset()) {
                this.gameView.getGrid().resetGrid();
            } else if (source == this.gameView.getControls().getSave()) {
                this.gameView.saveCurrentGrid();
            } else if (source == this.gameView.getControls().getHint()) {
                this.gameView.gridToHint();
            } else if (source instanceof HintButton) {
                this.hintButtonPressed((HintButton) source);
            } else if (source == this.gameView.getHint().getHintReturn()) {
                this.gameView.hintToGrid();
            }
        }
    }
    
}
