/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignmentgui;

import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JFrame;

/**
 *
 * @author dbson
 */
public class SudokuModel {
    public static SudokuDatabaseModel sudokuDB;
    
    public static GameFrame game;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        sudokuDB = new SudokuDatabaseModel();
        
        game = new GameFrame(sudokuDB);
        game.setVisible(true);
        game.setSize(900, 700);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /**
     * Change a string of numbers into a list of integer values.
     * 
     * @param toChange String of integers, formated to represent a sudoku grid.
     * @return Array List of integers, formated to represent a sudoku grid.
     */
    public static ArrayList<Integer> createIntegerList(String toChange) {
        ArrayList<Integer> cellValues = new ArrayList<>();
        
        StringTokenizer st = new StringTokenizer(toChange, " \n,.");
        
        while (st.hasMoreTokens()) {
            cellValues.add(Integer.parseInt(st.nextToken()));
        }
        
        return cellValues;
    }
    
}
