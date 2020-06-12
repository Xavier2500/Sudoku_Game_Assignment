/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignmentgui;

/**
 *
 * @author dbson
 */
public class User {
    private String username;
    private String password;
    private int score;
    
    public User (String username, String password, int score) {
        this.username = username;
        this.password = password;
        this.score = score;
    }
    
    public User(String username, String password) {
        this(username, password, 0);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
