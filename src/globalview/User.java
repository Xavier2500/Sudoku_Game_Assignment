package globalview;

/**
 * Represents a user currently operating the program. Only one instance of this
 * class is ever used at any single point in time. Contains the user's name and
 * password for database retrieval reasons.
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
    
    /**
     * Sets the score to whatever is entered. If the entered value is under
     * zero, it sets the score to zero.
     * 
     * @param score New score value for the user.
     */
    public void setScore(int score) {
        if (score > 0) {
            this.score = score;
        } else {
            this.score = 0;
        }
    }
}
