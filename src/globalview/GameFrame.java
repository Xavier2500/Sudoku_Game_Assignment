package globalview;


import controllers.*;
import databaseconnectivity.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Frame that contains everything the system view will use for the entire game.
 * This includes a total of three panels the frame will swap between depending
 on what the user needs to view at any given time. These panels are: the
 user input and userSelectionPanel selection panel, the full game panel, and a panel
 that shows once a user has completed a panel.
 * 
 * @author dbson
 */
public class GameFrame extends JFrame {
    private DifficultyPanel userSelectionPanel;
    private GameControlPanel game;
    private GridCompletedPanel gridCompleted;

    private SudokuDatabaseModel sudokuDB;

    private SudokuGrid workingGrid;
    private SudokuGrid completeGrid;
    private SudokuGrid resetGrid;

    private User currentUser;
    
    private char lastChosenDifficulty;

    public GameFrame(String frameName, SudokuDatabaseModel sudokuDB) {
        super(frameName);
        
        this.sudokuDB = sudokuDB;

        this.setLayout(null);
        this.setSize(1000, 1000);

        this.userSelectionPanel = new DifficultyPanel();
        this.userSelectionPanel.setSize(500, 400);
        this.setPanelLocation(this.userSelectionPanel);
        this.userSelectionPanel.setVisible(true);
        
        this.gridCompleted = new GridCompletedPanel();
        this.gridCompleted.setSize(450, 300);
        this.setPanelLocation(this.gridCompleted);
        this.gridCompleted.setVisible(false);
        
        this.add(this.userSelectionPanel);
        this.add(this.gridCompleted);
    }

    /**
     * Creates a new grid panel, and sets up the panels size and location in
     * the frame.
     */
    public void createGridPanel() {
        GridPanel gameGrid = new GridPanel(this.workingGrid, this.completeGrid, this.resetGrid);
        
        this.game = new GameControlPanel(gameGrid, this.sudokuDB, this.currentUser);
        this.game.setSize(this.game.getControls().getWidth(), 800);
        this.setPanelLocation(this.game);
        this.game.setVisible(true);

        this.add(this.game);
    }
    
    /**
     * Takes in a component, and calculates the right position for the component
     * to be placed into the center of the frame.
     * 
     * @param toPlace Component to set location of.
     */
    private void setPanelLocation(JComponent toPlace) {
        int frameWidth = this.getWidth();
        int frameHeight = this.getHeight();
        
        int componentWidth = toPlace.getWidth();
        int componentHeight = toPlace.getHeight();
        
        int xPosition = ((frameWidth - componentWidth) / 2);
        int yPosition = ((frameHeight - componentHeight) / 2);
        
        toPlace.setLocation(xPosition, yPosition);
    }
    
    /**
     * Reset the save states of the user, stored in the database, to show the
 user having not completed any databases of that userSelectionPanel.
     */
    public void resetUserPlayedGrids() {
        this.sudokuDB.resetPlayedGrids(this.currentUser, this.lastChosenDifficulty);
        this.resetChangeDifficulty();
    }
    
    /**
     * Return to choosing a userSelectionPanel after reaching the database reset panel.
     */
    public void resetChangeDifficulty() {
        this.userSelectionPanel.showDatabaseReset(false);
        this.lastChosenDifficulty = ' ';
    }
    
    /**
     * Update the text on the completion panel to show the users score, then
     * let the user view the panel.
     */
    public void showCompletedPanel() {
        this.gridCompleted.updateUserScoreView(this.currentUser);
        
        this.game.setVisible(false);
        this.userSelectionPanel.setVisible(false);
        this.gridCompleted.setVisible(true);
    }
    
    /**
     * Creates a new grid based on the selected userSelectionPanel. Also changes from
     * the user input panel to the game panel.
     */
    public void diffToGrid() {
        this.userSelectionPanel.setVisible(false);
        this.createGridPanel();
        this.game.getGrid().updateModel();
        this.gridCompleted.setVisible(false);
    }

    /**
     * Change view from the game and the user input panels.
     */
    public void gridToDiff() {
        this.game.setVisible(false);
        this.userSelectionPanel.setVisible(true);
        this.gridCompleted.setVisible(false);
    }

    /**
     * Sets the working, complete, and reset grid of the games model, based on
     * the requested userSelectionPanel type. If a save state from the user exists, the
     * state that was saved is put into the working grid value. If the user has
     * completed all grids of the requested userSelectionPanel type, the database reset
     * panel is shown to the user.
     *
     * @param diff The selected userSelectionPanel type for the sudoku grid.
     */
    public void setGrids(char diff) {
        ArrayList<String> gridValues = this.sudokuDB.requestedGrid(this.currentUser, diff);
        String saveState = null;

        if (gridValues != null) {
            if (gridValues.size() == 3) {
                saveState = gridValues.remove(gridValues.size() - 1);
            }

            for (String gridValue : gridValues) {
                ArrayList<Integer> cellValues = this.createIntegerList(gridValue);

                if (cellValues.contains(0)) {
                    this.workingGrid = new SudokuGrid(cellValues);
                    this.resetGrid = new SudokuGrid(cellValues);
                } else {
                    this.completeGrid = new SudokuGrid(cellValues);
                }
            }

            if (saveState != null) {
                ArrayList<Integer> savedCellValues = this.createIntegerList(saveState);
                this.workingGrid = new SudokuGrid(savedCellValues);
            }

            this.diffToGrid();
            
            if (saveState != null) {
                this.game.getControls().getSaveState().setText("Loaded save from database.");
            }
        } else {
            this.userSelectionPanel.showDatabaseReset(true);
        }
    }
    
    /**
     * Change a string of numbers into a list of integer values.
     * 
     * @param toChange String of integers, formated to represent a sudoku grid.
     * @return Array List of integers, formated to represent a sudoku grid.
     */
    public ArrayList<Integer> createIntegerList(String toChange) {
        ArrayList<Integer> cellValues = new ArrayList<>();
        
        StringTokenizer st = new StringTokenizer(toChange, " \n,.");
        
        while (st.hasMoreTokens()) {
            try {
                cellValues.add(Integer.parseInt(st.nextToken()));
            } catch (NumberFormatException ex) {
                System.err.println("Non-number passed and removed.");
            }
        }
        
        return cellValues;
    }
    
    /**
     * Creates the frame, and adds the controllers to the frame.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SudokuDatabaseModel sudokuDB = new SudokuDatabaseModel();
        
        GameFrame game = new GameFrame("Sudoku Game", sudokuDB);
        game.setVisible(true);
        game.setSize(1000, 1000);
        game.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        game.setResizable(false);
        game.setVisible(true);
        
        GlobalController globalControls = new GlobalController(game);
    }
    
    public DifficultyPanel getDifficulty() {
        return userSelectionPanel;
    }
    
    public GameControlPanel getGame() {
        return game;
    }

    public SudokuDatabaseModel getSudokuDB() {
        return sudokuDB;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setLastChosenDifficulty(char lastChosenDifficulty) {
        this.lastChosenDifficulty = lastChosenDifficulty;
    }

    public GridCompletedPanel getGridCompleted() {
        return gridCompleted;
    }
}
