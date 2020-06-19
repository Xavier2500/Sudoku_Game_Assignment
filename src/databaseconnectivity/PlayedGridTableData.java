/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseconnectivity;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Deals with all interactions with the Played Grids table. This includes:
 * checking if a user has completed a grid before, finding save states, reseting
 * a users completion for a given difficulty, and a few others.
 * 
 * @author dbson
 */
public class PlayedGridTableData {
    private Connection conn = null;

    private String tableName;
    private ArrayList<String> columns = new ArrayList<>();
    
    private final static int PLAYERID_COLUMN = 0;
    private final static int GRIDINFOID_COLUMN = 1;
    private final static int COMPLETION_COLUMN = 2;
    private final static int SAVESTATE_COLUMN = 3;
    
    public PlayedGridTableData(Connection conn) {
        this.conn = conn;
        
        this.tableName = "PlayedGrid";
        
        this.columns.add("player_id");
        this.columns.add("gridinfo_id");
        this.columns.add("completed");
        this.columns.add("save_state");
        
        this.createTable();
    }
    
    public void createTable() {
        try {
            Statement statement = this.conn.createStatement();
            
            if(!this.doesTableExist(tableName)) {
                String fkPlayerName = ("played_playerid_fk");
                String fkGridInfoName = ("played_gridinfoid_fk");

                String createTable = (""
                        + "CREATE TABLE " + this.tableName + " ("
                        + this.columns.get(PLAYERID_COLUMN) + " INT, "
                        + this.columns.get(GRIDINFOID_COLUMN) + " INT, "
                        + this.columns.get(COMPLETION_COLUMN) + " CHAR(1) NOT NULL, "
                        + this.columns.get(SAVESTATE_COLUMN) + " VARCHAR(161), "
                        + "CONSTRAINT " + fkPlayerName + " FOREIGN KEY "
                        + "(" + this.columns.get(PLAYERID_COLUMN) + ") REFERENCES "
                        + "Player (" + this.columns.get(PLAYERID_COLUMN) + "), "
                        + "CONSTRAINT " + fkGridInfoName + " FOREIGN KEY "
                        + "(" + this.columns.get(GRIDINFOID_COLUMN) + ") REFERENCES "
                        + "GridInfo (" + this.columns.get(GRIDINFOID_COLUMN) + "), "
                        + "CONSTRAINT " + this.tableName + "_pk PRIMARY KEY "
                        + "(" + this.columns.get(PLAYERID_COLUMN) + ", " + this.columns.get(GRIDINFOID_COLUMN) + "))");

                statement.executeUpdate(createTable);
            }
            
        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    /**
     * Add or update a save state into the database.
     * 
     * @param playerId Current users id number in Player
     * @param gridId Current grids id number in GridInfo
     * @param isComplete Current completion state of the grid
     * @param saveState Current value state of grid, if incomplete
     */
    public void savePlayedGrid(int playerId, int gridId, boolean isComplete, String saveState) {
        try {
            Statement statement = this.conn.createStatement();
            String storedSaveState = this.saveStateExits(playerId, gridId);
            
            char completed;
            if (isComplete) {
                completed = 'y';
                saveState = null;
            } else {
                completed = 'n';
                saveState = ("'" + saveState + "'");
            }
            
            if (storedSaveState == null) {
                String addState = (""
                        + "INSERT INTO " + this.tableName + " ("
                        + this.columns.get(PLAYERID_COLUMN) + ", "
                        + this.columns.get(GRIDINFOID_COLUMN) + ", "
                        + this.columns.get(COMPLETION_COLUMN) + ", "
                        + this.columns.get(SAVESTATE_COLUMN) + ") "
                        + "VALUES (" + playerId + ", " + gridId + ", "
                        + "'" + completed + "', "
                        + saveState + ")");
                
                statement.executeUpdate(addState);
            } else {
                String updateState = (""
                        + "UPDATE " + this.tableName + " "
                        + "SET " + this.columns.get(COMPLETION_COLUMN) + " = "
                        + "'" + completed + "', "
                        + this.columns.get(SAVESTATE_COLUMN) + " = "
                        + saveState + " "
                        + "WHERE " + this.columns.get(PLAYERID_COLUMN) + " = "
                        + playerId + " "
                        + "AND " + this.columns.get(GRIDINFOID_COLUMN) + " = "
                        + gridId + "");
                
                statement.executeUpdate(updateState);
            }
            
            statement.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    /**
     * Remove rows storing grid completion and save states for a given user of
     * a given difficulty.
     * 
     * @param userId Player to remove save data from.
     * @param gridIds List of grid id's of a specific difficulty type.
     */
    public void removeUserGrids(int userId, ArrayList<Integer> gridIds) {
        try {
            Statement statement = this.conn.createStatement();
            
            String findAllGrids = (""
                    + "DELETE FROM " + this.tableName + " "
                    + "WHERE " + this.columns.get(PLAYERID_COLUMN) + " = "
                    + "" + userId + " "
                    + "AND (");
            
            for (int i = 0; i < gridIds.size(); i++) {
                findAllGrids += (""
                        + this.columns.get(GRIDINFOID_COLUMN) + " = "
                        + gridIds.get(i) + " ");
                
                if ((i + 1) != gridIds.size()) {
                    findAllGrids += "OR ";
                } else {
                    findAllGrids += ")";
                }
            }
            
            statement.executeUpdate(findAllGrids);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    /**
     * Searches for the completed grids from the user with the given id. Returns
     * a list of all grid id's of grids the user has completed.
     * 
     * @param userId Current users player id in database.
     * @return List of grid id's for grids the user has completed.
     */
    public ArrayList<Integer> completedGrids(int userId) {
        ArrayList<Integer> gridId = new ArrayList<>();
        try {
            Statement statement = this.conn.createStatement();
            
            String searchPlayerGrid = (""
                    + "SELECT " + this.columns.get(GRIDINFOID_COLUMN) + ", "
                    + this.columns.get(COMPLETION_COLUMN) + " "
                    + "FROM " + this.tableName + " "
                    + "WHERE " + this.columns.get(PLAYERID_COLUMN) + " = "
                    + userId + "");
            
            ResultSet rs = statement.executeQuery(searchPlayerGrid);
            
            while(rs.next()) {
                if (rs.getString(this.columns.get(COMPLETION_COLUMN)).equalsIgnoreCase("y")) {
                    gridId.add(rs.getInt(this.columns.get(GRIDINFOID_COLUMN)));
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        
        return gridId;
    }
    
    /**
     * Finds the current save state of a grid, should the user have made a save
     * state of the grid. Should no save state be found, null is returned.
     * 
     * @param playerId Player ID in database of current user.
     * @param gridId Grid ID in database of current grid.
     * @return String with available save state. Null if no save state is found.
     */
    public String saveStateExits(int playerId, int gridId) {
        String saveState = null;
        
        try {
            Statement statement = this.conn.createStatement();
            
            String findSaveState = (""
                    + "SELECT " + this.columns.get(COMPLETION_COLUMN) + ", "
                    + this.columns.get(SAVESTATE_COLUMN) + " "
                    + "FROM " + this.tableName + " "
                    + "WHERE " + this.columns.get(PLAYERID_COLUMN) + " = "
                    + "" + playerId + " "
                    + "AND " + this.columns.get(GRIDINFOID_COLUMN) + " = "
                    + "" + gridId);
            
            ResultSet rs = statement.executeQuery(findSaveState);
            while (rs.next()) {
                if (rs.getString(this.columns.get(COMPLETION_COLUMN)).equalsIgnoreCase("n")) {
                    saveState = rs.getString(this.columns.get(SAVESTATE_COLUMN));
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        
        return saveState;
    }
    
    private boolean doesTableExist(String newTableName) {
        boolean flag = false;
        try {

            System.out.println("Scanning database...");
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet rsDBMeta = dbmd.getTables(null, null, null, null);

            while (rsDBMeta.next()) {
                String tableName = rsDBMeta.getString("TABLE_NAME");
                if (tableName.compareToIgnoreCase(newTableName) == 0) {
                    System.out.println(tableName + "  already exists. Loading...");
                    flag = true;
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return flag;
    }
}
