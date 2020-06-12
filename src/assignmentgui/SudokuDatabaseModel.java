/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignmentgui;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Controls all database interaction. Database creation/updates/queries are done
 * through here.
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
     * See contents of a table.
     *
     * @param tableName
     */
    public void viewTable(String tableName) {
        try {
            Statement statement = conn.createStatement();
            String query = ("SELECT * FROM " + tableName);

            ResultSet rs = statement.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i < rsmd.getColumnCount(); i++) {
                    System.out.print(rs.getObject(i).toString() + ", ");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.err.println("Error");
        }
    }

    /**
     * Searches database for sudoku grids of the given difficulty type. Returns
     * the starting grid and finish grid for the randomly selected grid.
     * 
     * If the user has a save state of the grid being loaded, it adds the grid
     * to the end of the returned list.
     *
     * @param currentUser
     * @param diffLevel The requested sudoku difficulty level.
     * @return Both the starting and complete grid arrays. Also a save state,
     * where applicable.
     */
    public ArrayList<String> requestedGrid(User currentUser, char diffLevel) {
        ArrayList<String> cellValues = new ArrayList<>();
        ArrayList<Integer> idValues = new ArrayList<>();

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
            
            Collections.shuffle(idValues);
            cellValues = this.gridInfoTable.getGameGrid(idValues.get(0));

        } catch (SQLException e) {
            System.err.println("Error");
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
}
