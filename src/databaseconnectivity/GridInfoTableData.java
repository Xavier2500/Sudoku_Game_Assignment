/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseconnectivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Controls interactions with the sudoku table that stores all grid types and
 * information. Contains methods designed specifically for what the system needs
 * to access.
 * 
 * @author dbson
 */
public class GridInfoTableData {
    private Connection conn = null;

    private String tableName;
    private ArrayList<String> columns = new ArrayList<>();

    private String[] diffLevels = new String[]{"easy", "medium", "hard"};
    
    private final static int ID_COLUMN = 0;
    private final static int START_COLUMN = 1;
    private final static int COMPLETE_COLUMN = 2;
    private final static int DIFFICULTY_COLUMN = 3;
    
    public GridInfoTableData(Connection conn) {
        this.conn = conn;
        
        this.tableName = "GridInfo";
        
        this.columns.add("gridinfo_id");
        this.columns.add("startgrid");
        this.columns.add("completegrid");
        this.columns.add("difficulty_level");
        
        this.createGridInfoTable();
        for (String diffLevel : diffLevels) {
            ArrayList<String> gridValues = this.loadGrids(diffLevel);
            String[] gridList = new String[gridValues.size()];
            gridList = gridValues.toArray(gridList);

            this.generateGridInfo(diffLevel.charAt(0), gridList);
        }
    }
    
    /**
     * After checking if the table already exists, it creates the table, ready
     * for any data of the types to be entered into the table.
     */
    public void createGridInfoTable() {
        try {
            Statement statement = this.conn.createStatement();

            if (!this.checkTableExisting(this.tableName)) {
                String tableCreationSql = (""
                        + "CREATE TABLE " + this.tableName + " ("
                        + this.columns.get(ID_COLUMN) + " INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
                        + this.columns.get(START_COLUMN) + " VARCHAR(161) NOT NULL, "
                        + this.columns.get(COMPLETE_COLUMN) + " VARCHAR(161) NOT NULL, "
                        + this.columns.get(DIFFICULTY_COLUMN) + " CHAR(1) NOT NULL, "
                        + "CONSTRAINT " + this.columns.get(ID_COLUMN) + "_pk PRIMARY KEY (" + this.columns.get(ID_COLUMN) + "))");

                statement.executeUpdate(tableCreationSql);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Populates the table with the information needed: a grids difficulty level,
     * as well as the start ans=d end states of the grid.
     * 
     * @param diffLevel Difficulty level of the grids being entered.
     * @param allGrids Start and end states for all grids read from the corresponding
     * difficulty file.
     */
    private void generateGridInfo(char diffLevel, String... allGrids) {
        boolean isStartGrid = true;
        String startGrid = null;
        String completeGrid = null;

        try {
            Statement statement = conn.createStatement();

            for (String currentGrid : allGrids) {
                if (isStartGrid) {
                    startGrid = currentGrid;
                    isStartGrid = false;
                } else {
                    try {
                        completeGrid = currentGrid;

                        if (!this.compareGrids(completeGrid, diffLevel)) {
                            String sqlAddRow = (""
                                    + "INSERT INTO " + this.tableName + "("
                                    + this.columns.get(START_COLUMN) + ", "
                                    + this.columns.get(COMPLETE_COLUMN) + ", "
                                    + this.columns.get(DIFFICULTY_COLUMN) + ") "
                                    + "VALUES ("
                                    + "'" + startGrid + "', "
                                    + "'" + completeGrid + "', "
                                    + "'" + diffLevel + "')");

                            System.out.println(sqlAddRow);

                            statement.executeUpdate(sqlAddRow);
                        }
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    } finally {
                        isStartGrid = true;
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    /**
     * Get the id's of all grids in the table that are of the requested
     * difficulty level.
     * 
     * @param difficultyLevel Requested difficulty level for grids
     * @return ArrayList of grid ID's meeting the requirements.
     */
    public ArrayList<Integer> queryGridDifficulties(char difficultyLevel) {
        ArrayList<Integer> idValues = new ArrayList<>();
        
        try {
            Statement statement = conn.createStatement();
            String difficultyGridIds = (""
                    + "SELECT " + this.columns.get(ID_COLUMN) + " "
                    + "FROM " + this.tableName + " "
                    + "WHERE " + this.columns.get(DIFFICULTY_COLUMN) + " = "
                    + "'" + difficultyLevel + "'");

            ResultSet rs = statement.executeQuery(difficultyGridIds);
            while (rs.next()) {
                idValues.add(rs.getInt(this.columns.get(ID_COLUMN)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        
        return idValues;
    }
    
    /**
     * Retrieve the start and complete grid values of a row with a given grid id.
     * 
     * @param gridId Requested grid id to retrieve information from.
     * @return The start and complete grid versions.
     */
    public ArrayList<String> getGameGrid(int gridId) {
        ArrayList<String> cellValues = new ArrayList<>();
        try {
            Statement statement = this.conn.createStatement();
            
            String getGameGrid = (""
                    + "SELECT " + this.columns.get(START_COLUMN) + ", "
                    + this.columns.get(COMPLETE_COLUMN) + " "
                    + "FROM " + this.tableName + " "
                    + "WHERE " + this.columns.get(ID_COLUMN) + " = "
                    + gridId + "");
            
            ResultSet rs = statement.executeQuery(getGameGrid);
            while (rs.next()) {
                cellValues.add(rs.getString(this.columns.get(START_COLUMN)));
                cellValues.add(rs.getString(this.columns.get(COMPLETE_COLUMN)));
            }
        } catch (SQLException ex) {
            System.out.println();
        }
        return cellValues;
    }
    
    /**
     * Retrieve the id of a grid using the grids start and complete forms.
     * 
     * @param startGrid Grids starting state.
     * @param completeGrid Grids complete state.
     * @return Id related to the row with a start and complete grid equal to that
     * of the inputed values.
     */
    public int getGridId(String startGrid, String completeGrid) {
        int gridId = 0;
        try {
            Statement statement = this.conn.createStatement();
            
            String getGameGrid = (""
                    + "SELECT " + this.columns.get(ID_COLUMN) + " "
                    + "FROM " + this.tableName + " "
                    + "WHERE " + this.columns.get(START_COLUMN) + " = "
                    + "'" + startGrid + "' "
                    + "AND " + this.columns.get(COMPLETE_COLUMN) + " = "
                    + "'" + completeGrid + "'");
            
            ResultSet rs = statement.executeQuery(getGameGrid);
            while (rs.next()) {
                gridId = rs.getInt(this.columns.get(ID_COLUMN));
            }
        } catch (SQLException ex) {
            System.out.println();
        }
        return gridId;
    }
    
    /**
     * Retrieve a list of grid id's for grids of the given difficulty.
     * 
     * @param difficultyType The requested difficulty type.
     * @return List of id values for all grids with the difficulty type requested.
     */
    public ArrayList<Integer> getGridIds(char difficultyType) {
        ArrayList<Integer> gridIds = new ArrayList<>();
        try {
            Statement statement = this.conn.createStatement();
            
            String getGameGrid = (""
                    + "SELECT " + this.columns.get(ID_COLUMN) + " "
                    + "FROM " + this.tableName + " "
                    + "WHERE " + this.columns.get(DIFFICULTY_COLUMN) + " = "
                    + "'" + difficultyType + "'");
            
            ResultSet rs = statement.executeQuery(getGameGrid);
            while (rs.next()) {
                gridIds.add(rs.getInt(this.columns.get(ID_COLUMN)));
            }
        } catch (SQLException ex) {
            System.out.println();
        }
        return gridIds;
    }
    
    /**
     * Check to see if a given grid for a difficulty level has already been
     * entered into the database.
     *
     * @param completedGrid Complete state of a grid.
     * @param diffLevel Difficulty level of the grid.
     * @return True if the grid already exists. False if it does not.
     */
    public boolean compareGrids(String completedGrid, char diffLevel) {
        boolean gridExists = false;
        try {
            Statement statement = conn.createStatement();
            String sameGridQuery = (""
                    + "SELECT " + this.columns.get(COMPLETE_COLUMN) + ", "
                    + this.columns.get(DIFFICULTY_COLUMN) + " "
                    + "FROM " + this.tableName + " "
                    + "WHERE " + this.columns.get(COMPLETE_COLUMN) + " = "
                    + "'" + completedGrid + "' "
                    + "AND " + this.columns.get(DIFFICULTY_COLUMN) + " = "
                    + "'" + diffLevel + "'");

            ResultSet rs = statement.executeQuery(sameGridQuery);
            while (rs.next()) {
                gridExists = true;
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return gridExists;
    }
    
    /**
     * Read grid information from the requested text file.
     *
     * @param fileName The requested file to draw information from.
     * @return List of the start and complete state of all grids stored in the
     * text file.
     */
    private ArrayList<String> loadGrids(String fileName) {
        ArrayList<String> gridValues = new ArrayList<>();
        fileName = (fileName + ".txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String nextGrid = "";

            while (!((nextGrid = br.readLine()) == null)) {
                gridValues.add(nextGrid);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return gridValues;
    }
    
    
    /**
     * Checks the database to see if a table already exists.
     * 
     * @param newTableName Name of a table about to be created.
     * @return 
     */
    private boolean checkTableExisting(String newTableName) {
        boolean flag = false;
        try {

            System.out.println("check existing tables.... ");
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet rsDBMeta = dbmd.getTables(null, null, null, null);

            while (rsDBMeta.next()) {
                String tableName = rsDBMeta.getString("TABLE_NAME");
                if (tableName.compareToIgnoreCase(newTableName) == 0) {
                    System.out.println(tableName + "  is there");
                    flag = true;
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return flag;
    }
}
