/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import extrabuttons.DifficultyButton;
import globalview.GameControlPanel;
import globalview.GameFrame;
import globalview.User;
import databaseconnectivity.SudokuDatabaseModel;
import extrabuttons.ChangeDifficultyButton;
import extrabuttons.ChangeUserButton;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import listenerclasses.GlobalListener;

/**
 * Deals with all interactions at the global scale. This includes interactions
 * that would change the view between different global panels. It also deals with
 * what the system should do when the user decides to leave the game.
 * 
 * @author dbson
 */
public class GlobalController extends GlobalListener{
    private GameFrame globalView;
    
    private GameController gameView;
    
    private GameControlPanel gamePanel;
    
    public GlobalController(GameFrame globalView) {
        this.globalView = globalView;
        
        this.globalView.addWindowListener(this);
        
        this.globalView.getDifficulty().getUsername().addKeyListener(this);
        this.globalView.getDifficulty().getPassword().addKeyListener(this);
        
        DifficultyButton[] options = this.globalView.getDifficulty().getDifficultyOptions();
        for (DifficultyButton option : options) {
            option.addActionListener(this);
        }
        
        this.globalView.getDifficulty().getConfirmUser().addActionListener(this);
        
        this.globalView.getGridCompleted().getChangeUserButton().addActionListener(this);
        this.globalView.getGridCompleted().getChangeDifficultyButton().addActionListener(this);
        this.globalView.getGridCompleted().getLeaveButton().addActionListener(this);
        
        this.globalView.getDifficulty().getResetCompletionPanel().getChangeDifficulty().addActionListener(this);
        this.globalView.getDifficulty().getResetCompletionPanel().getResetDatabase().addActionListener(this);
    }

    public void setGridListeners() {
        this.globalView.getGame().getControls().getUserChange().addActionListener(this);
        this.globalView.getGame().getControls().getDiffChange().addActionListener(this);
        this.globalView.getGame().getControls().getCheckProg().addActionListener(this);
    }
    
    public void createGameController() {
        this.gamePanel = this.globalView.getGame();
        this.gameView = new GameController(this.gamePanel);
    }
    
    /**
     * Used to make the Action Listener look less cluttered, by separating the
     * actions performed on the global view when a difficulty button is
     * pressed.
     * 
     * @param buttonPressed Reference the the difficulty button pressed.
     */
    private void difficultyButtonPressed(DifficultyButton buttonPressed) {
        this.globalView.setGrids(buttonPressed.getDifficultyType());
        
        if (this.globalView.getGame() != null) {
            this.setGridListeners();
            this.createGameController();
        } else {
            this.globalView.setLastChosenDifficulty(buttonPressed.getDifficultyType());
            this.globalView.getDifficulty().showDatabaseReset(true);
        }
    }
    
    /**
     * Controls what actions must be done when the user would like to check
     * their progress. Also decides whether the grid must be saved or not.
     */
    private void checkProgressPressed() {
        boolean isComplete = this.gamePanel.showCompletion();
        
        if (isComplete) {
            this.globalView.getGame().saveCurrentGrid();
            User currentUser = this.globalView.getCurrentUser();
            currentUser.setScore(currentUser.getScore() + 10);
            this.globalView.showCompletedPanel();
        }
    }
    
    /**
     * Changes the global view back to a state where the user must enter their
     * account information.
     */
    private void changeUserPressed() {
        this.globalView.gridToDiff();
        this.globalView.getDifficulty().confirmDataAccessibility(true);
        this.globalView.getDifficulty().difficultyButtonAccessibility(false);
        this.globalView.getDifficulty().resetInputFields();
    }
    
    /**
     * Changes the global view back to a state where the user must select what
     * difficulty they would like to try.
     */
    private void changeDifficultyPressed() {
        if (this.gameView != null) {
            this.globalView.gridToDiff();
        }
        this.globalView.getDifficulty().confirmDataAccessibility(false);
        this.globalView.getDifficulty().difficultyButtonAccessibility(true);
        this.globalView.resetChangeDifficulty();
    }
    
    /**
     * Influences the database to reset the user's completed grids of a given
     * difficulty type.
     */
    private void resetDatabasePressed() {
        this.globalView.resetUserPlayedGrids();
    }
    
    /**
     * Checks if the game grid is complete, if there, and creates a save state
     * for the user even if it isn't complete. Saves user score information
     * before leaving as well.
     */
    private void leaveGame() {
        if (this.gamePanel != null) {
            this.checkProgressPressed();
            this.globalView.getGame().saveCurrentGrid();
        }
        
        if (this.globalView.getCurrentUser() != null) {
            this.globalView.getSudokuDB().updateUserScore(this.globalView.getCurrentUser());
        }
    }
    
    /**
     * Used specifically for the username and password entry fields. Restricts
     * the text fields to 20 and 61 characters respectively, as to make sure data
     * entered into the fields conforms with the length of the databases
     * VARCHAR fields.
     * 
     * @param e Key event, which tells us when a key was pressed.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        JTextField source = (JTextField) e.getSource();
        Integer fieldLength = null;
        Integer currentLength = source.getText().length();
        
        if (source == this.globalView.getDifficulty().getUsername()) {
            fieldLength = 20;
        } else if (source == this.globalView.getDifficulty().getPassword()) {
            fieldLength = 16;
        }
        
        if (currentLength.equals(fieldLength)) {
            e.consume();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source instanceof DifficultyButton) {
            DifficultyButton buttonPressed = (DifficultyButton) source; 
            this.difficultyButtonPressed(buttonPressed);
        } else if (source == this.globalView.getDifficulty().getConfirmUser()) {
            SudokuDatabaseModel toGetUser = this.globalView.getSudokuDB();
            User currentUser = this.globalView.getDifficulty().checkUserInfo(toGetUser);
            this.globalView.setCurrentUser(currentUser);
        } else if (source instanceof ChangeDifficultyButton) {
            this.changeDifficultyPressed();
        } else if(source == this.globalView.getDifficulty().getResetCompletionPanel().getResetDatabase()) {
            this.resetDatabasePressed();
        } else if (source == this.globalView.getGridCompleted().getLeaveButton()) {
            this.globalView.dispatchEvent(new WindowEvent(this.globalView, WindowEvent.WINDOW_CLOSING));
        } else if (this.globalView.getGame() != null) {
            if (source instanceof ChangeUserButton) {
                this.changeUserPressed();
            } else if (source == this.gamePanel.getControls().getCheckProg()) {
                this.checkProgressPressed();
            }
        }
    }
    @Override
    public void windowClosing(WindowEvent e) {
        JFrame frame = (JFrame) e.getSource();
        
        int result = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to leave the game?",
                "Exit Sudoku Program",
                JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            this.leaveGame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }
}
