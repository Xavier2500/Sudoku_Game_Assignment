/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extrabuttons;

import javax.swing.JButton;

/**
 * Button that models a hint button. It stores the index the button is at in the
 * sudoku grid, which allows for the button to represent a specific cell in the
 * Sudoku Grid model.
 * 
 * @author dbson
 */
public class HintButton extends JButton{
    private int rowNo;
    private int columnNo;
    
    public HintButton(String buttonTitle, int rowNo, int columnNo) {
        super(buttonTitle);
        
        this.rowNo = rowNo;
        this.columnNo = columnNo;
    }
    
    /**
     * Get the row number the button is assigned to.
     * 
     * @return The row number value given to the button.
     */
    public int getRowNo() {
        return rowNo;
    }
    
    /**
     * Get th column number the button is assigned to.
     * 
     * @return The column number value given to the button.
     */
    public int getColumnNo() {
        return columnNo;
    }
}
