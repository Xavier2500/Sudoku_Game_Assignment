/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package globalview;

import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

/**
 * Stores the layout of the text fields that the user will interact with for the
 * majority of the program. Contains the start and complete states of the grid,
 * and the state the user is currently effecting.
 * 
 * @author dbson
 */
public class GridPanel extends JPanel{
    private JTextField[][] cells = new JTextField[SudokuGrid.NUMBER_OF_ROWS][SudokuGrid.ROW_LENGTH];
    private SudokuGrid workingGrid;
    private SudokuGrid completeGrid;
    private SudokuGrid resetGrid;
    
    
    public GridPanel (SudokuGrid wGrid, SudokuGrid cGrid, SudokuGrid rGrid) {
        this.workingGrid = wGrid;
        this.completeGrid = cGrid;
        this.resetGrid = rGrid;
        
        this.setLayout(new GridLayout(9, 9, 25, 25));
        this.setBackground(new Color(0, 0, 0, 0));
        
        for (int i = 0; i < SudokuGrid.NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < SudokuGrid.ROW_LENGTH; j++) {
                int value = this.resetGrid.getCell(i, j);
                
                JTextField nextCell = new JTextField("");
                nextCell.setFont(new Font("Arial", Font.PLAIN, 20));
                nextCell.setHorizontalAlignment(SwingConstants.CENTER);
                
                if (value == 0) {
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
        this.updateView();
    }
    
    /**
     * Retrieves the user inputed values in the text fields, and saves them into
     * the working grid.
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
     * Checks the working grid for any changes in values compared to the current
     * view. Changes the view to represent the values in the working grid.
     */
    public void updateView() {
        for (int i = 0; i < SudokuGrid.NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < SudokuGrid.ROW_LENGTH; j++) {
                JTextField currentField = this.cells[i][j];
                String cellView = ("" + currentField.getText());
                String cellValue = ("" + this.workingGrid.getCell(i, j));
                
                if (!cellView.equalsIgnoreCase(cellValue)) {
                    if (cellValue.equalsIgnoreCase("0")) {
                        cellValue = "";
                    }
                    currentField.setText(cellValue);
                }
            }
        }
    }
    
    /**
     * Gets the information stored in the reset grid, and stores it into the
     * working grid.
     */
    public void resetGrid() {
        ArrayList<Integer> resetValues = this.resetGrid.getAllRowValues();
        this.workingGrid.setAllCells(resetValues);
        this.updateView();
    }
    
    /**
     * Checks the percentage amount of the grid that has been completed
     * accurately by the user. If the grid is complete, the value returned is
     * 1, to show that each user entered value is equal to that of its system
     * counterpart.
     * 
     * @return Value containing completeness of the sudoku grid.
     */
    public double checkCompletion() {
        this.updateModel();
        
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

    public JTextField[][] getCells() {
        return cells;
    }

    public SudokuGrid getWorkingGrid() {
        return workingGrid;
    }

    public SudokuGrid getCompleteGrid() {
        return completeGrid;
    }

    public SudokuGrid getResetGrid() {
        return resetGrid;
    }
}
