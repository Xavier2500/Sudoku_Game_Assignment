package globalview;

import databaseconnectivity.SudokuDatabaseModel;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Hold the graphical items that show the game in it's entirety. This includes
 * the grid, the extra controls located bellow the grid, and the components
 * required for the "hint" functionality of the system.
 * 
 * @author dbson
 */
public class GameControlPanel extends JPanel {
    private GridPanel grid;
    private SudokuBackground background;
    private ExtrasPanel controls;
    private HintBackground hint;
    
    private SudokuDatabaseModel sudokuDB;
    
    private User currentUser;
    
    public GameControlPanel(GridPanel grid, SudokuDatabaseModel sudokuDB, User currentUser) {
        this.sudokuDB = sudokuDB;
        this.currentUser = currentUser;
        
        this.setLayout(null);
        
        this.grid = null;
        this.controls = null;
        
        this.createBackground(grid);
        
        if (this.controls == null) {
            this.controls = new ExtrasPanel();
            this.controls.setVisible(true);
        }
        
        this.add(this.background);
        this.add(this.controls);
        this.add(this.hint);
    }       
    
    /**
     * When the grid has been created, it applies it to a background panel and
     * a hint panel. It also creates the control panel, and sets the position of
     * both the background and hint panel.
     * 
     * @param grid The game grid that has been selected and created due to the
     * users difficulty choice.
     */
    public void createBackground(GridPanel grid) {
        if (grid != null) {
            this.grid = grid;
            
            this.background = new SudokuBackground(this.grid);
            this.background.setLayout(null);
            this.background.setSize(this.background.getImageWidth(), this.background.getImageHeight());
            this.background.setVisible(true);
            
            this.setExtrasOptions();
            this.calculateGamePosition(this.background);
            this.createHint(grid);
            this.calculateGamePosition(this.hint);
        }
    }
    
    /**
     * Generate the hint panel, and set it's size and position
     * 
     * @param grid Currently running grid for the game.
     */
    public void createHint(GridPanel grid) {
        this.hint = new HintBackground(grid);
        this.hint.setLayout(null);
        this.hint.setSize(this.hint.getImageWidth(), this.hint.getImageHeight() + 50);
        this.hint.setVisible(false);
    }
    
    /**
     * Swaps from the grid panel to the hint panel, and updates the hint panel
     * to represent the current state of all user inputed values.
     */
    public void gridToHint() {
        this.background.setVisible(false);
        this.controls.setVisible(false);
        this.hint.setVisible(true);
        
        this.hint.updateHintLabel();
        this.hint.updateHintView();
    }
    
    /**
     * Swap from the hint panel to grid panel.
     */
    public void hintToGrid() {
        this.background.setVisible(true);
        this.controls.setVisible(true);
        this.hint.setVisible(false);
    }
    
    /**
     * Uses a hint to set one cell to it's completed value. If no hints are left
     * after use, it will deactivate all hints.
     * 
     * @param row Row of selected cell to change.
     * @param column Column of selected cell to change.
     */
    public void hintUsed(int row, int column) {
        int availableHints = this.hint.useHint(row, column);
        
        if (availableHints == 0) {
            this.hint.deactivateAllHints();
        }
        
        this.hintToGrid();
    }
    
    /**
     * Sets the location and size of the extra controls for the sudoku game.
     */
    private void setExtrasOptions() {
        if (this.controls == null) {
            this.controls = new ExtrasPanel();
            this.controls.setVisible(true);
        }
        
        this.controls.setLocation(0, (this.background.getHeight() + 20));
        this.controls.setSize((this.background.getImageWidth() + 350), 100);
    }
    
    /**
     * Calculates the position for a panel, to be set in the center of the
     * control panel.
     */
    private void calculateGamePosition(JComponent toSetPosition) {
        int xPos = ((this.controls.getWidth() - toSetPosition.getWidth()) / 2);
        int yPos = 0;
        
        toSetPosition.setLocation(xPos, yPos);
    }
    
    /**
     * Calculate grid completion percentage, then show the new completion
     * percentage to the current user.
     */
    public boolean showCompletion() {
        double result = this.grid.checkCompletion();
        boolean isComplete = false;
        
        DecimalFormat df = new DecimalFormat("##.##%");
        String completionPercentage = df.format(result);
        
        String completionText = ("Current completion is " + completionPercentage);
        
        this.controls.getCompletion().setText(completionText);
        
        if (result == 1.0) {
            isComplete = true;
        }
        
        return isComplete;
    }
    
    /**
     * CChecks grid completion, and saves the grid into the database.
     */
    public void saveCurrentGrid() {
        boolean gridComplete = false;
        
        if (this.grid.checkCompletion() == 1.0) {
            gridComplete = true;
        }
        
        ArrayList<Integer> start = this.grid.getResetGrid().getAllRowValues();
        ArrayList<Integer> working = this.grid.getWorkingGrid().getAllRowValues();
        ArrayList<Integer> complete = this.grid.getCompleteGrid().getAllRowValues();
        
        this.sudokuDB.saveCurrentState(gridComplete, currentUser, start, 
                working, complete);
        
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();
        
        String saveText = ("Grid saved at: " + dateFormat.format(now));
        
        this.controls.getSaveState().setText(saveText);
    }
    
    public GridPanel getGrid() {
        return grid;
    }

    public SudokuBackground getBackgroundPanel() {
        return background;
    }

    public ExtrasPanel getControls() {
        return controls;
    }

    public HintBackground getHint() {
        return hint;
    }
}
