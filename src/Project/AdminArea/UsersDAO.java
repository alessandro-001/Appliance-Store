package Project.AdminArea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

/**
 * ----- USERS DAO CLASS -----
 * 
 * @author Alessandro Frondini - 14501682
 * Data Access Object (DAO) class used to interacting with the database in the user table.
 * It provides the methods to retrieve, add and check the user informations.
 */
public class UsersDAO {
	
	/**
	 * ----- DATABASE CONNECTION -----
	 * Establish a connection with the store SQLite database 
	 * @return a connection object to the DB or null if the connection is faulty.
	 */
	private Connection connect() {
        Connection connection = null;

        try {
            String url = "jdbc:sqlite:store.sqlite";
            connection = DriverManager.getConnection(url);
            System.out.println("\nDatabase Connected Successfully! :)\n\n///---  Extracting Data from Customer ---///\n");       

        } catch (SQLException se) {
            System.err.println(se.getClass().getName() + ": " + se.getMessage());
        }
        return connection;
    }

	
    /*-------------------------------------------- CRUD operations --------------------------------------------*/ 

	
	/**
	 * ----- GET a single User -----
	 * Retrieves a single user by the username from the DB
	 * @param username (is the username to retrieve)
	 * @return user details as an object or null if the user don't exist in the DB
	 * @throws SQLException (if an error occurs in the DB)
	 */
	public Users getUserByUsername(String username) throws SQLException {
	    Users user = null;
	    String sql = "SELECT username, password, role "
	    	   	   + "FROM users WHERE username = ?";

	    try (Connection conn = this.connect();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	    	pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            String dbUsername = rs.getString("username");
	            String dbPassword = rs.getString("password");
	            String dbRole = rs.getString("role");

	            user = new Users(dbUsername, dbPassword, dbRole);
	            
	            //Debug to remove
	            System.out.println("Username from DB: " + dbUsername);
	            System.out.println("Password (hashed) from DB: " + dbPassword);
	            System.out.println("Role from DB: " + dbRole);

	        }

	    } catch (SQLException se) {
	        se.printStackTrace();
	    }

	    return user; 
	}
		
	
	/**
	 * ----- GET USER ROLE by ID -----
	 * Retrieves the user role by their ID
	 * @param userId (is the ID of the user).
	 * @return the user role (or return guest as default if user don't exist).
	 * @throws SQLException (if a database error occurs).
	 */ 
    public String getUserRoleById(String userId) throws SQLException {
        String role = "guest"; // Default role
        String query = "SELECT role FROM users "
        		     + "WHERE userID = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:store.sqlite");
             PreparedStatement pstmt = conn.prepareStatement(query)) {
        	pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    role = rs.getString("role");
                }
            }
        } catch (SQLException se) {
            se.printStackTrace();
            throw se; 
        }

        return role;
    }
	
    
    /** 
     * ----- UNIQUE USERNAMES CHECK -----
     * Check if the username already exist in the DB
     * @param username (is the username to check).
     * @return true if username exist or false if don't.
     * @throws SQLException (if a database error occurs). 
     */
    public boolean usernameTaken(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM users "
        			 + "WHERE username = ?";
        
        try (Connection conn = this.connect();
        	PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;  // If > 0 the username exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error checking if username exists");
        }

        return false;
    }

    
	/**
	 * ---- ADD new Users + auto hash password ----
	 * Adds a new username to the DB with the password encrypted with BCrypt.
	 * @param username (the new user's username).
	 * @param password (plain text password to be successfully hashed before storing in the DB).
	 * @param role (is the role of the new user).
	 * @throws SQLException (if a database error occurs). 
	 */
	public void addUser(String username, String password, String role) throws SQLException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt()); //Password hash before db upload
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

        	pstmt.setString(1, username);
        	pstmt.setString(2, hashedPassword); //Hashed pw
        	pstmt.setString(3, role);
        	System.out.println("Hashed Password ---> " + hashedPassword);
        	pstmt.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
            throw new SQLException("Error adding the user");
        }
    }

	
	/**
	 * ---- GET USERNAME by ID ----
	 * Retrieves the user's username by their username in the DB
	 * @param usernameParam (is the parameter to search in the DB).
	 * @return the username or return null if the user don't exist in the DB.
	 * @throws SQLException (if a database error occurs). 
	 */
	public String getUsernameById(String usernameParam) throws SQLException {
        String query = "SELECT username FROM users WHERE username = ?";
        String username = null;

        try (Connection conn = this.connect();
        	 PreparedStatement pstmt = conn.prepareStatement(query)) {
        	
            pstmt.setString(1, usernameParam); 
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                username = rs.getString("username"); //Extract the username
            }
            
        } catch (SQLException se) {
           se.printStackTrace();
           throw se; 
        }

        return username;
    }
	
 
	/**
	 * ----- GET USER PASSWORD by Username -----
	 * Retrieves the encrypted password from the DB by the user's username
	 * @param username (the new user's username).
	 * @return the encrypted password or null if user don't exist.
	 * @throws SQLException (if a database error occurs). 
	 */
	public String getUsersPassword(String username) throws SQLException {
	    String password = null;
	    String query = "SELECT password FROM users WHERE username = ?";

	    try (Connection conn = this.connect();
	         PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            password = rs.getString("password"); // Extract the hashed password 
	        }
	    } catch (SQLException se) {
	        se.printStackTrace();
	        throw se; 
	    }

	    return password;
	}

}
