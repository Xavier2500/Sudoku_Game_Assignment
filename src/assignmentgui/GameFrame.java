/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignmentgui;

import java.awt.event.*;
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
    
    public GameFrame(SudokuDatabaseModel sudokuDB) {
        this.sudokuDB = sudokuDB;
        
        this.setLayout(null);
        
        this.completion  = new JLabel("Testing, testing");
        this.completion.setSize(200, 20);
        this.completion.setLocation(150, 450);
        this.completion.setHorizontalAlignment(SwingConstants.CENTER);
        this.completion.setVisible(true);
        
        this.difficulty = new DifficultyPanel();
        this.difficulty.getEasyButton().addActionListener(this);
        this.difficulty.setSize(300, 100);
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
    
    public void diffToGrid() {
        this.difficulty.setVisible(false);
        this.createGridPanel();
        this.gameGrid.updateModel();
        this.gameGrid.setVisible(true);
        this.controls.setVisible(true);
    }
    
    public void gridToDiff() {
        this.controls.setVisible(false);
        this.gameGrid.setVisible(false);
        this.difficulty.setVisible(true);
    }
    
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
//        this.completion
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == this.difficulty.getEasyButton()) {
            this.setGrids('e');
        } else if (source == this.difficulty.getMediumButton()) {
            this.setGrids('m');
        } else if (source == this.difficulty.getHardButton()) {
            this.setGrids('h');
        } else if (source == this.controls.getUserChange()) {
            //TODO
        } else if (source == this.controls.getDiffChange()) {
            this.gridToDiff();
        } else if (source == this.controls.getReset()) {
            this.workingGrid.resetGrid(this.resetGrid.getAllRowValues());
            this.gameGrid.updateView();
        } else if (source == this.controls.getCheckProg()) {
            //TODO
        }
    }
}
