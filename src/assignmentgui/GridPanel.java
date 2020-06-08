/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignmentgui;

import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author dbson
 */
public class GridPanel extends JPanel implements ActionListener, KeyListener{
    private JTextField[][] cells = new JTextField[SudokuGrid.NUMBER_OF_ROWS][SudokuGrid.ROW_LENGTH];
    private SudokuGrid workingGrid;
    private SudokuGrid completeGrid;
    private SudokuGrid resetGrid;
    
    public GridPanel (SudokuGrid wGrid, SudokuGrid cGrid, SudokuGrid rGrid) {
        this.workingGrid = wGrid;
        this.completeGrid = cGrid;
        this.resetGrid = rGrid;
        
        this.setLayout(new GridLayout(9, 9, 20, 20));
        
        for (int i = 0; i < SudokuGrid.NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < SudokuGrid.ROW_LENGTH; j++) {
                int value = this.workingGrid.getCell(i, j);
                
                JTextField nextCell = new JTextField("");
                nextCell.setHorizontalAlignment(SwingConstants.CENTER);
                
                if (value == 0) {
                    nextCell.addKeyListener(this);
                    cells[i][j] = nextCell;
                } else {
                    String sValue = Integer.toString(value);
                    nextCell.setText(sValue);
                    nextCell.setEditable(false);
                    cells[i][j] = nextCell;
                }
                
                this.add(this.cells[i][j]);
            }
        }
    }
    
    /**
     * Sets the values of the user sudoku grid to be that of what has been
     * inputted into the text fields.
     */
    public void updateModel() {
        for (int i = 0; i < SudokuGrid.NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < SudokuGrid.ROW_LENGTH; j++) {
                JTextField currentField = this.cells[i][j];
                String cellValue = ("" + currentField.getText());
                
                if (cellValue.length() == 0) {
                    cellValue = "0";
                }
                if (cellValue.length() == 1) {
                    if (this.workingGrid.getCell(i, j) != Integer.parseInt(cellValue)) {
                        this.workingGrid.setCell(i, j, cellValue);
                    }
                }
            }
        }
    }
    
    /**
     * Set the values of the panels to be the same as the current sudoku model
     * being influenced by the user.
     */
    public void updateView() {
        for (int i = 0; i < SudokuGrid.NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < SudokuGrid.ROW_LENGTH; j++) {
                JTextField currentField = this.cells[i][j];
                String cellView = ("" + currentField.getText());
                String cellValue = ("" + this.workingGrid.getCell(i, j));
                
                if (!cellView.equalsIgnoreCase(cellValue)) {
                    currentField.setText("");
                }
            }
        }
    }
    
    /**
     * Checks the percentage amount of the grid that has been completed
     * accurately by the user.
     */
    public synchronized double checkCompletion() {
        ArrayList<Integer> userGrid = this.workingGrid.getAllRowValues();
        ArrayList<Integer> systemGrid = this.completeGrid.getAllRowValues();
        ArrayList<Integer> startGrid = this.resetGrid.getAllRowValues();
        
        double correctCells = 0.0;
        double emptyCells = 0.0;
        
        for (int i = 0; i < userGrid.size(); i++) {
            if (startGrid.get(i) == 0) {
                if (userGrid.get(i).equals(systemGrid.get(i))) {
                    correctCells++;
                }
                emptyCells++;
            }
        }
        
        double currentCompletion = (correctCells / emptyCells);
        
        return currentCompletion;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        
        this.updateModel();
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

}
