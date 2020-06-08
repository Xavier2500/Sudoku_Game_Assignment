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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Controls all database interaction. Database creation/updates/queries are
 * done through here.
 * @author dbson
 */
public class SudokuDatabaseModel {
    private Connection conn = null;
    
    private final String url = "jdbc:derby:SudokuDB;create=true";
    private final String dbusername = "pdc";
    private final String dbpassword = "pdc";
    
    private String[] tableNames = new String[] {"GridDescription"};
    private static int newGridRow = 0;
    private String[] diffLevels = new String[] {"easy", "medium", "hard"};
    
    public SudokuDatabaseModel() {
        this.createTables();
        for (String diffLevel : diffLevels) {
            ArrayList<String> gridValues = this.loadGrids(diffLevel);
            String[] gridList = new String[gridValues.size()];
            gridList = gridValues.toArray(gridList);
            
            this.generateGridInfo(diffLevel.charAt(0), gridList);
        }
    }
    
    private boolean checkTableExisting(String newTableName) {
        boolean flag = false;
        try {

            System.out.println("check existing tables.... ");
            String[] types = {"TABLE"};
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet rsDBMeta = dbmd.getTables(null, null, null, null);//types);
            //Statement dropStatement=null;
            while (rsDBMeta.next()) {
                String tableName = rsDBMeta.getString("TABLE_NAME");
                if (tableName.compareToIgnoreCase(newTableName) == 0) {
                    System.out.println(tableName + "  is there");
                    flag = true;
                }
            }
            if (rsDBMeta != null) {
                rsDBMeta.close();
            }
        } catch (SQLException ex) {
        }
        return flag;
    }
    
    public void createTables() {
        try {
            conn = DriverManager.getConnection(url, dbusername, dbpassword);
            
            Statement statement = conn.createStatement();
            
            for (String tableName : tableNames) {
                String tablePK = (tableName.toLowerCase() + "_id");
                String constraintPK = (tableName + "_" + tableName.toLowerCase() + "id_pk");
                
                // TODO Change DB to have CHAR identifier instead of three tables.
                if (!checkTableExisting(tableName)) {
                    String createTableCode = ("CREATE TABLE " + tableName + " ("
                            + tablePK + " INT, ");
                    
                    switch(tableName) {
                        case "GridDescription":
                            createTableCode += "startgrid VARCHAR(161),"
                                    + "completegrid VARCHAR(161),"
                                    + "diffcode CHAR(1) NOT NULL";
                            break;
                        case "Player":
                            createTableCode += "username VARCHAR(20) NOT NULL"
                                    + ", password VARCHAR(16) NOT NULL";
                            break;
                        case "PlayerGrid":
                            createTableCode = "CREATE TABLE " + tableName + " ("
                                    + "player_id INT,"
                                    + "griddescription_id INT,"
                                    + "gridcomplete CHAR(1) NOT NULL,"
                                    + "currentgridstate VARCHAR(161)";
                            break;
                    }
                    
                    if (!tableName.equalsIgnoreCase("PlayerGrid")) {
                        createTableCode += (", CONSTRAINT " + constraintPK
                                + " PRIMARY KEY (" + tablePK + "))");
                    } else if (tableName.equalsIgnoreCase("PlayerGrid")) {
                        createTableCode += (", CONSTRAINT playergrid_playerid_fk FOREIGN KEY player_id REFERENCES Player (player_id)"
                                + ", CONSTRAINT playergrid_gridid_fk FOREIGN KEY griddescription_id REFERENCES GridDescription (griddescription_id)"
                                + ", CONSTRAINT playergrid_playergriddescid_pk PRIMARY KEY (player_id, griddescription_id))");
                    }
                    
                    statement.executeUpdate(createTableCode);
                }

                this.viewGrid(tableName);
            }
            
            statement.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    /**
     * Takes in a table name as well as both starting and complete grids to be
     * saved into their respective databases, should the table exist, and the
     * grid has not already been entered.
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
                if(isStartGrid) {
                    startGrid = currentGrid;
                    isStartGrid = false;
                } else {
                    try {
                        completeGrid = currentGrid;
                        
                        if (!this.compareGrids(completeGrid, diffLevel)) {
                            SudokuDatabaseModel.newGridRow++;
                            String sqlAddRow = "INSERT INTO GridDescription VALUES "
                                    + "(" + SudokuDatabaseModel.newGridRow + ", '" + startGrid + "', '"
                                    + completeGrid + "', '" + diffLevel + "')";

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
            //Logger.getLogger(SudokuDatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                    + "SELECT completegrid, diffcode "
                    + "FROM GridDescription "
                    + "WHERE completegrid = '" + completedGrid + "' "
                    + "AND diffcode = '" + diffLevel + "'");
            
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
     * See contents of a singular grid.
     * 
     * @param tableName 
     */
    public void viewGrid(String tableName) {
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
    
    public ArrayList<String> requestedGrid(char diffLevel) {
        ArrayList<String> cellValues = new ArrayList<>();
        ArrayList<Integer> idValues = new ArrayList<>();
        
        try {
            Statement statement = conn.createStatement();
            String query = ("SELECT griddescription_id "
                    + "FROM GridDescription "
                    + "WHERE diffcode = '" + diffLevel + "'");
            
            ResultSet rs = statement.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                idValues.add(rs.getInt(1));
            }
            
            Collections.shuffle(idValues);
            query = ("SELECT startgrid, completegrid "
                    + "FROM GridDescription "
                    + "WHERE diffcode = '" + diffLevel + "'"
                    + "AND griddescription_id = " + idValues.get(0) + "");
            rs = statement.executeQuery(query);
            while (rs.next()) {
                cellValues.add(rs.getString("startgrid"));
                cellValues.add(rs.getString("completegrid"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error");
        }
        
        return cellValues;
    }
    
    /**
     * Read grid values from sudoku files.
     * 
     * @param fileName
     * @return 
     */
    private ArrayList<String> loadGrids (String fileName) {
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
}
