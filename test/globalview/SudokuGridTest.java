/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package globalview;

import java.util.ArrayList;
import java.util.InputMismatchException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dbson
 */
public class SudokuGridTest {
    private ArrayList<Integer> testGrid;
    
    public SudokuGridTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.testGrid = new ArrayList<>();
        
        while (this.testGrid.size() < 81) {
            this.testGrid.add(5);
        }
        
        System.out.println(this.testGrid.size());
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getCell method when given an acceptable row and column number. As
     * the test grid is set up with all fives, it should only give out a five.
     */
    @Test
    public void testGetCell() {
        System.out.println("getCell: Correct Input");
        int row = 0;
        int column = 0;
        SudokuGrid instance = new SudokuGrid(this.testGrid);
        int expResult = 5;
        int result = instance.getCell(row, column);
        assertEquals(expResult, result);
    }

    /**
     * Test of setCell method when given a value that is number. No exceptions
     * should be thrown, and it should go through with no problems.
     */
    @Test
    public void setCellSuccess() {
        System.out.println("setCell: Number Input");
        int row = 0;
        int column = 0;
        String value = "7";
        SudokuGrid instance = new SudokuGrid(this.testGrid);
        
        try {
            instance.setCell(row, column, value);
        } catch (InputMismatchException ex) {
            System.err.println(ex.getMessage());
            fail("Error was hit.");
        }
    }
    
    /**
     * Test of setCell method when given a value that is not a number. Should
     * throw a InputMismatchException, due to an incorrect input.
     */
    @Test
    public void setCellIncorrectInput() {
        System.out.println("setCell: Non-Number Input");
        int row = 0;
        int column = 0;
        String value = "L";
        SudokuGrid instance = new SudokuGrid(this.testGrid);
        
        try {
            instance.setCell(row, column, value);
            fail("Error was not hit.");
        } catch (InputMismatchException ex) {
            System.err.println(ex.getMessage());
            System.out.println("Error was corectly reached.");
        }
    }
}
