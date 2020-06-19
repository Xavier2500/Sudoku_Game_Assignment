/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package globalview;

import databaseconnectivity.SudokuDatabaseModel;
import java.util.ArrayList;
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
public class GameFrameTest {
    private GameFrame frameInstance;
    
    public GameFrameTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.frameInstance = new GameFrame("Sudoku Grid", new SudokuDatabaseModel());
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test the createIntegerList method. Designed to take in a string of
     * numbers, and split it into each character, and save the number into a
     * list of integers
     */
    @Test
    public void createIntegerListSuccess() {
        System.out.println("createIntegerList: No errors");
        String toChange = "2,4,6,9,7,8,4,3,2,7,8,9,5";
        
        ArrayList<Integer> expResult = new ArrayList<>();
        expResult.add(2);
        expResult.add(4);
        expResult.add(6);
        expResult.add(9);
        expResult.add(7);
        expResult.add(8);
        expResult.add(4);
        expResult.add(3);
        expResult.add(2);
        expResult.add(7);
        expResult.add(8);
        expResult.add(9);
        expResult.add(5);
        
        
        ArrayList<Integer> result = this.frameInstance.createIntegerList(toChange);
        assertEquals(expResult, result);
    }
    
    /**
     * Test the createIntegerList method with incorrect input. Should activate
     * an error, which is caught, and the value, since it is not a number, is
     * not added to the integer list.
     */
    @Test
    public void createIntegerListIncorrectInput() {
        System.out.println("createIntegerList: Incorrect Input");
        String toChange = "2,4,6,9,7,8,h,3,2,7,l,9,5";
        
        ArrayList<Integer> expResult = new ArrayList<>();
        expResult.add(2);
        expResult.add(4);
        expResult.add(6);
        expResult.add(9);
        expResult.add(7);
        expResult.add(8);
        expResult.add(3);
        expResult.add(2);
        expResult.add(7);
        expResult.add(9);
        expResult.add(5);
        
        
        ArrayList<Integer> result = this.frameInstance.createIntegerList(toChange);
        assertEquals(expResult, result);
    }
}
