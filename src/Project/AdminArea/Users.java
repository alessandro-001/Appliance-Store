package Project.AdminArea;

/**
 * ----- USERS CLASS -----
 * @author Alessandro Frondini 
 * This class represent the User in the system, it comprises the username, password and role.
 */
public class Users {
    private String username;
    private String password;
    private String role; 

    /**
     * The Constructor for the Users class
     * @param username (username of the user in the database).
     * @param password (password of the user in the database).
     * @param role (role of the user in the database).
     */
    public Users(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    /**
     * Retrieves the user's username
     * @return the username
     */
    public String getUsersName() {
        return username;
    }

    /**
     * Retrieves the user's password
     * @return the password
     */
    public String getUsersPassword() {
        return password;
    }

    /**
     * Retrieves the user's role
     * @return the user role
     */
    public String getUsersRole() {
        return role;
    }

    /**
     * @return a string with the user details.
     */
    @Override
    public String toString() {
        return "User -> username='" + username + "', role='" + role;
    }
}
