/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignmentgui;

import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author dbson
 */
public class GameFrame extends JFrame implements ActionListener {
    private GridPanel gameGrid;
    private DifficultyPanel difficulty;
    private ExtrasPanel controls;
    
    private SudokuDatabaseModel sudokuDB;
    
    private JLabel completion;
    
    private SudokuGrid workingGrid;
    private SudokuGrid completeGrid;
    private SudokuGrid resetGrid;
    
    private User currentUser;
    
    public GameFrame(SudokuDatabaseModel sudokuDB) {
        this.sudokuDB = sudokuDB;
        
        this.setLayout(null);
        
        this.completion  = new JLabel("Testing, testing");
        this.completion.setSize(200, 20);
        this.completion.setLocation(150, 450);
        this.completion.setHorizontalAlignment(SwingConstants.CENTER);
        this.completion.setVisible(true);
        
        this.difficulty = new DifficultyPanel();
        for (DifficultyButton currentDifficulty : this.difficulty.getChooseDifficulty()) {
            currentDifficulty.addActionListener(this);
        }
        this.difficulty.getConfirmUser().addActionListener(this);
        this.difficulty.setSize(300, 150);
        this.difficulty.setLocation(50, 50);
        this.difficulty.setVisible(true);
        
        this.controls = new ExtrasPanel();
        this.controls.getUserChange().addActionListener(this);
        this.controls.getDiffChange().addActionListener(this);
        this.controls.getReset().addActionListener(this);
        this.controls.getCheckProg().addActionListener(this);
        this.controls.setSize(800, 30);
        this.controls.setLocation(0, 500);
        this.controls.setVisible(false);
        
        this.add(this.difficulty);
        this.add(this.controls);
        this.add(this.completion);
    }
    
    public void createGridPanel() {
        this.gameGrid = new GridPanel(this.workingGrid, this.completeGrid, this.resetGrid);
        this.gameGrid.setSize(400, 400);
        this.gameGrid.setLocation(10, 10);
        this.gameGrid.setVisible(false);
        this.add(this.gameGrid);
    }
    
    /**
     * Changes view from the user and difficulty selection view to the actual
     * game view.
     */
    public void diffToGrid() {
        this.difficulty.setVisible(false);
        this.createGridPanel();
        this.gameGrid.updateModel();
        this.gameGrid.setVisible(true);
        this.controls.setVisible(true);
    }
    
    /**
     * Change view from actual game view to the user and difficulty selection
     * view.
     */
    public void gridToDiff() {
        this.controls.setVisible(false);
        this.gameGrid.setVisible(false);
        this.difficulty.setVisible(true);
    }
    
    /**
     * Sets the working, complete, and reset grid of the games model, based on
     * the requested difficulty type.
     * 
     * @param diff The selected difficulty type for the sudoku grid.
     */
    public void setGrids(char diff) {
        ArrayList<String> gridValues = this.sudokuDB.requestedGrid(diff);
        
        for (String gridValue : gridValues) {
            ArrayList<Integer> cellValues = SudokuModel.createIntegerList(gridValue);
            
            if (cellValues.contains(0)) {
                this.workingGrid = new SudokuGrid(cellValues);
                this.resetGrid = new SudokuGrid(cellValues);
            } else {
                this.completeGrid = new SudokuGrid(cellValues);
            }
        }
        
        this.diffToGrid();
    }
    
    public void showCompletion() {
        double result = this.gameGrid.checkCompletion();
        
        DecimalFormat df = new DecimalFormat("##.##%");
        String completionPercentage = df.format(result);
        
        String completionText = ("Current completion is " + completionPercentage);
        
        this.completion.setText(completionText);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source instanceof DifficultyButton) {
            this.setGrids(((DifficultyButton) source).getDifficultyType());
        } else if (source == this.controls.getUserChange()) {
            this.gridToDiff();
            this.difficulty.confirmDataAccessibility(false);
            this.difficulty.difficultyPanelAccessibility(true);
        } else if (source == this.controls.getDiffChange()) {
            this.gridToDiff();
            this.difficulty.confirmDataAccessibility(false);
            this.difficulty.difficultyPanelAccessibility(true);
        } else if (source == this.controls.getReset()) {
            this.workingGrid.resetGrid(this.resetGrid.getAllRowValues());
            this.gameGrid.updateView();
        } else if (source == this.controls.getCheckProg()) {
            this.showCompletion();
        } else if (source == this.difficulty.getConfirmUser()) {
            this.difficulty.checkUserInfo(this.sudokuDB);
        }
    }
}
