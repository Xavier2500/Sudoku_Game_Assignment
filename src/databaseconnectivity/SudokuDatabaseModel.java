package databaseconnectivity;

import globalview.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Interacts with all parts of the database. Mainly used to create disjointed
 * JOIN effects, by calling the required query from one table, and using the
 * result into another query.
 *
 * @author dbson
 */
public class SudokuDatabaseModel {

    private Connection conn = null;

    private final String url = "jdbc:derby:SudokuDB;create=true";
    private final String dbusername = "pdc";
    private final String dbpassword = "pdc";

    private String[] tableNames = new String[]{"PlayerGrid"};

    private PlayerTableData playerTable;
    private GridInfoTableData gridInfoTable;
    private PlayedGridTableData playedGridTable;

    public SudokuDatabaseModel() {
        try {
            conn = DriverManager.getConnection(url, dbusername, dbpassword);

            this.playerTable = new PlayerTableData(this.conn);
            this.gridInfoTable = new GridInfoTableData(this.conn);
            this.playedGridTable = new PlayedGridTableData(this.conn);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

    }
    
    /**
     * Remove the save states of a current user, for the purpose of replaying
     * completed grids.
     * 
     * @param currentUser User currently playing the program.
     * @param difficultyType Grid difficulty type to reset.
     */
    public void resetPlayedGrids(User currentUser, char difficultyType) {
        String username = currentUser.getUsername();
        String password = currentUser.getPassword();
        
        int playerId = this.playerTable.queryPlayerId(username, password);
        
        ArrayList<Integer> diffGridIds = this.gridInfoTable.getGridIds(difficultyType);
        
        this.playedGridTable.removeUserGrids(playerId, diffGridIds);
    }
    
    /**
     * Searches database for sudoku grids of the given difficulty type. Returns
     * the starting grid and finish grid for the randomly selected grid. If the
     * user has a save state of the grid being loaded, it adds the grid to the
     * end of the returned list.
     *
     * @param currentUser Player currently using the program.
     * @param diffLevel The requested sudoku difficulty level.
     * @return Both the starting and complete grid arrays. Also a save state,
     * where applicable.
     */
    public ArrayList<String> requestedGrid(User currentUser, char diffLevel) {
        ArrayList<String> cellValues = new ArrayList<>();
        ArrayList<Integer> idValues;

        try {
            Statement statement = conn.createStatement();

            idValues = this.gridInfoTable.queryGridDifficulties(diffLevel);

            ArrayList<Integer> completedGrids = this.checkUserGrids(currentUser);

            for (Integer completedGrid : completedGrids) {
                for (int i = 0; i < idValues.size(); i++) {
                    if (idValues.get(i).equals(completedGrid)) {
                        idValues.remove(i);
                    }
                }
            }
            
            if (!idValues.isEmpty()) {
                Collections.shuffle(idValues);
                cellValues = this.gridInfoTable.getGameGrid(idValues.get(0));
                
                int playerId = this.playerTable.queryPlayerId(currentUser.getUsername(), currentUser.getPassword());
                int gridId = this.gridInfoTable.getGridId(cellValues.get(0), cellValues.get(1));
                
                String saveState = this.playedGridTable.saveStateExits(playerId, gridId);
                
                if (saveState != null) {
                    cellValues.add(saveState);
                }
            } else {
                cellValues = null;
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return cellValues;
    }

    /**
     * Checks what grids a user has completed. Returns an ArrayList with the row
     * id of each completed grid. If they haven't completed any, ArrayList is
     * returned empty.
     *
     * @param currentUser User currently operating the program.
     * @return ArrayList containing the ID of every grid the user has completed.
     */
    public ArrayList<Integer> checkUserGrids(User currentUser) {
        int userId = 0;
        ArrayList<Integer> completedGrids = new ArrayList<>();

        userId = this.playerTable.queryPlayerId(currentUser.getUsername(), currentUser.getPassword());

        if (userId != 0) {
            completedGrids = this.playedGridTable.completedGrids(userId);
        }

        return completedGrids;
    }

    /**
     * Find out if the database has a user with the given username and password.
     * If the user exists, and the password is correct, the user is returned. If
     * the user does not exist, they are created in the database. If the user
     * exists, but the password is incorrect, the method returns null.
     *
     * @param username Requested or new username of the user.
     * @param password Given password of existing user, or new password for new
     * user.
     * @return A User object if an existing or new user. Null if the given
     * password was incorrect.
     */
    public User checkUser(String username, String password) {
        User currentUser = null;

        String userPassword = this.playerTable.queryPlayerExists(username);

        if (userPassword == null) {
            currentUser = new User(username, password);
            this.playerTable.addNewPlayer(currentUser);
        } else if (userPassword.equals(password)) {
            int currentScore = this.playerTable.queryPlayerScore(username, password);
            currentUser = new User(username, password, currentScore);
        }

        return currentUser;
    }

    /**
     * Searches through database for user and sudoku grid currently being used.
     * Then saves the information into the "Played Grids" database for future
     * program use.
     * 
     * @param isComplete Whether the grid is complete.
     * @param currentUser Player currently using the program.
     * @param startingGrid The starting (beginning) state of the current grid.
     * @param workingGrid The current state of the grid being played.
     * @param completeGrid The finished version of the current grid.
     */
    public void saveCurrentState(boolean isComplete, User currentUser, ArrayList<Integer> startingGrid, ArrayList<Integer> workingGrid, ArrayList<Integer> completeGrid) {
        String start = this.modelToDatabseForm(startingGrid);
        String working = this.modelToDatabseForm(workingGrid);
        String complete = this.modelToDatabseForm(completeGrid);
        
        int playerId = this.playerTable.queryPlayerId(currentUser.getUsername(), currentUser.getPassword());
        int gridId = this.gridInfoTable.getGridId(start, complete);
        
        this.playedGridTable.savePlayedGrid(playerId, gridId, isComplete, working);
    }
    
    /**
     * Save the new score from a user into their area of the database.
     * 
     * @param currentUser Player currently using the program.
     */
    public void updateUserScore(User currentUser) {
        String username = currentUser.getUsername();
        String password = currentUser.getPassword();
        
        int player_id = this.playerTable.queryPlayerId(username, password);
        this.playerTable.updatePlayerScore(player_id, currentUser.getScore());
    }
    
    /**
     * Change a list of integers, representing a sudoku grid, into a string, to
     * be used inside the database.
     *
     * @param toChange
     * @return
     */
    private String modelToDatabseForm(ArrayList<Integer> toChange) {
        String databaseForm = "";

        for (int i = 0; i < toChange.size(); i++) {
            String toAdd = (toChange.get(i) + "");

            if ((i + 1) != toChange.size()) {
                toAdd += ",";
            }

            databaseForm += toAdd;
        }

        return databaseForm;
    }
}
