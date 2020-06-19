package globalview;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Iterator;

/**
 * Stores the model of a sudoku grid.
 *
 * @author dbson
 */
public class SudokuGrid {
    private Integer[/*Row*/][/*Column*/] grid = new Integer[9][9];
    public static final int ROW_LENGTH = 9;
    public static final int NUMBER_OF_ROWS = 9;

    /**
     * Used to change the formatting of the number in the sudoku grid to
     * something that more easily represents a sudoku grid.
     * 
     * @param gridNumbers The number of the sudoku grid, to be formatted.
     */
    public SudokuGrid(ArrayList<Integer> gridNumbers) {
        Iterator<Integer> gridNumIt = gridNumbers.iterator();
        
        for (int i = 0; i < SudokuGrid.NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < SudokuGrid.ROW_LENGTH; j++) {
                this.grid[i][j] = gridNumIt.next();
            }
        }
    }
    
    /**
     * Change the values of all cells.
     * 
     * @param nextValues A list of the grids original values.
     */
    public void setAllCells(ArrayList<Integer> nextValues) {
        Iterator<Integer> gridNumIt = nextValues.iterator();
        
        for (int i = 0; i < SudokuGrid.NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < SudokuGrid.ROW_LENGTH; j++) {
                this.grid[i][j] = gridNumIt.next();
            }
        }
    }
    
    /**
     * Get the value of a specified cell in the grid.
     * 
     * @param row The row index of the requested cell.
     * @param column The column index of the requested cell.
     * @return Value of the requested cell.
     */
    public int getCell(int row, int column) {
        return this.grid[row][column];
    }
    
    /**
     * Check the new value of a given cell, and set it if the new value is valid.
     * 
     * @param row The row index of the requested cell.
     * @param column The column index of the requested cell.
     * @param value The new value to change the cell to.
     * @throws InputMismatchException 
     */
    public void setCell(int row, int column, String value) throws InputMismatchException {
        if (value.matches("[0-9]")) {
            this.grid[row][column] = Integer.parseInt(value);
        } else {
            throw new InputMismatchException("Given value cannot be entered. Please select a number from 1 to 9.");
        }
    }
    
    /**
     * Format the grid into an easily movable format.
     * 
     * @return A list of the grid in the same format as the number set in the
     * sudoku files.
     */
    public ArrayList<Integer> getAllRowValues() {
        ArrayList<Integer> allValues = new ArrayList<>();
        
        for (int i = 0; i < SudokuGrid.NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < SudokuGrid.ROW_LENGTH; j++) {
                allValues.add(this.grid[i][j]);
            }
        }
        
        return allValues;
    }
}
