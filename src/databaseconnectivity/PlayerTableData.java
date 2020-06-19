/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseconnectivity;

import globalview.User;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Controls all interaction with the player table. Includes adding new players,
 * saving new user scores and different queries to get things such as player
 * id's or passwords.
 * 
 * @author dbson
 */
public class PlayerTableData {
    private Connection conn = null;

    private String tableName;
    private ArrayList<String> columns = new ArrayList<>();

    private final static int ID_COLUMN = 0;
    private final static int USERNAME_COLUMN = 1;
    private final static int PASSWORD_COLUMN = 2;
    private final static int SCORE_COLUMN = 3;

    public PlayerTableData(Connection conn) {
        this.conn = conn;

        this.tableName = "Player";

        this.columns.add("player_id");
        this.columns.add("username");
        this.columns.add("password");
        this.columns.add("score");

        this.createPlayerTable();
    }

    /**
     * After checking if the table already exists, creates the table, so it is
     * ready for data entry.
     */
    public void createPlayerTable() {
        try {
            Statement statement = this.conn.createStatement();

            if (!this.checkTableExisting(this.tableName)) {
                String tableCreationSql = (""
                        + "CREATE TABLE " + this.tableName + " ("
                        + this.columns.get(PlayerTableData.ID_COLUMN) + " INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
                        + this.columns.get(PlayerTableData.USERNAME_COLUMN) + " VARCHAR(20) NOT NULL, "
                        + this.columns.get(PlayerTableData.PASSWORD_COLUMN) + " VARCHAR(16) NOT NULL, "
                        + this.columns.get(PlayerTableData.SCORE_COLUMN) + " INT, "
                        + "CONSTRAINT " + this.columns.get(PlayerTableData.ID_COLUMN) + "_pk PRIMARY KEY (" + this.columns.get(PlayerTableData.ID_COLUMN) + "))");

                statement.executeUpdate(tableCreationSql);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Adds a player into the database.
     * 
     * @param newUser User to be added to the database.
     */
    public void addNewPlayer(User newUser) {
        String newUsername = newUser.getUsername();
        String newPassword = newUser.getPassword();

        try {
            Statement statement = this.conn.createStatement();
            String insertPlayer = (""
                    + "INSERT INTO " + this.tableName + " ("
                    + this.columns.get(USERNAME_COLUMN) + ", "
                    + this.columns.get(PASSWORD_COLUMN) + ", "
                    + this.columns.get(SCORE_COLUMN) + ") "
                    + "VALUES ("
                    + "'" + newUsername + "', "
                    + "'" + newPassword + "', "
                    + "0)");

            statement.executeUpdate(insertPlayer);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    /**
     * Update a user's score when leaving the program.
     * 
     * @param playerId Id of the user who is now leaving the program.
     * @param newScore New score of the user.
     */
    public void updatePlayerScore(int playerId, int newScore) {
        try {
            Statement statement = this.conn.createStatement();
            String insertPlayer = (""
                    + "UPDATE " + this.tableName + " "
                    + "SET " + this.columns.get(SCORE_COLUMN) + " = "
                    + newScore + " "
                    + "WHERE " + this.columns.get(ID_COLUMN) + " = "
                    + playerId + "");

            statement.executeUpdate(insertPlayer);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    /**
     * Finds the score of the user currently using the program.
     * 
     * @param username Current users username.
     * @param password Current users password.
     * @return The score of the user, if they are in the database. Returns 0 if
     * user is not found (if they are new).
     */
    public int queryPlayerScore(String username, String password) {
        int currentScore = 0;

        try {
            Statement statement = conn.createStatement();

            String findUserSql = (""
                    + "SELECT " + this.columns.get(SCORE_COLUMN) + " "
                    + "FROM " + this.tableName + " "
                    + "WHERE " + this.columns.get(USERNAME_COLUMN) + " = '" + username + "' "
                    + "AND " + this.columns.get(PASSWORD_COLUMN) + " = '" + password + "'");

            ResultSet rs = statement.executeQuery(findUserSql);
            while (rs.next()) {
                currentScore = rs.getInt(this.columns.get(SCORE_COLUMN));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return currentScore;
    }
    
    /**
     * Checks the database to see if a given username is already present. This
     * is to stop people from using the same username as other users.
     * 
     * @param username Username that has just been entered by user.
     * @return Password of the user if the name exists. Null if the user doesn't
     * exist yet.
     */
    public String queryPlayerExists(String username) {
        String userPassword = null;
        try {
            Statement statement = conn.createStatement();

            String findUserSql = (""
                    + "SELECT " + this.columns.get(USERNAME_COLUMN)
                    + ", " + this.columns.get(PASSWORD_COLUMN) + " "
                    + "FROM " + this.tableName + " "
                    + "WHERE " + this.columns.get(USERNAME_COLUMN) + " = '" + username + "'");

            ResultSet rs = statement.executeQuery(findUserSql);
            while (rs.next()) {
                userPassword = rs.getString(this.columns.get(PASSWORD_COLUMN));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return userPassword;
    }
    
    /**
     * Searches the database for a given user.
     * 
     * @param username A given username.
     * @param password A given password.
     * @return Id of the user if they exist in the database. Returns 0 if they
     * do not exist.
     */
    public int queryPlayerId(String username, String password) {
        int userId = 0;
        
        try {
            Statement statement = this.conn.createStatement();
            
            String getIdSql = (""
                    + "SELECT " + this.columns.get(ID_COLUMN) + " "
                    + "FROM " + this.tableName + " "
                    + "WHERE " + this.columns.get(USERNAME_COLUMN) + " = '" + username + "' "
                    + "AND " + this.columns.get(PASSWORD_COLUMN) + " = '" + password + "'");
            
            ResultSet rs = statement.executeQuery(getIdSql);
            while (rs.next()) {
                userId = rs.getInt(this.columns.get(ID_COLUMN));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        
        return userId;
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
