/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignmentgui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
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
     * If the table already exists, it is not created.
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
     * Takes in a table name as well as both starting and complete grids to be
     * saved into their respective databases, should the table exist, and the
     * grid has not already been entered.
     *
     * @param tableName
     * @param allGrids
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
     * Check to see if a given grid for a difficulty level has already been
     * entered into the database.
     *
     * @param completedGrid
     * @param diffLevel
     * @return
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
     * Read grid values from sudoku files.
     *
     * @param fileName
     * @return
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
        }
        return flag;
    }
}
