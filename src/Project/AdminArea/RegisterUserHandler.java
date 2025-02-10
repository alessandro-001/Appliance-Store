package Project.AdminArea;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

/**
 * ----- REGISTER USER HANDLER -----
 * @author Alessandro Frondini - 14501682
 * The RegisterUserHandler handles the requests for the application, it supports:
 * - Display the registration form via HTTP GET requests.
 * - Process the user registration vis HTTP POST requests.
 * - Let the new user choose the username, password and role.
 * - Bootstrap CSS for styling the HTML response.
 */

public class RegisterUserHandler implements HttpHandler {
	
	/**
	 * @param he the HttpExchange object is used to manage the HTTP requests and responses.
	 * @throws IOException if an I/O error occurs while the request is being processed.
	 */
	@Override
    public void handle(HttpExchange he) throws IOException {
        // Handle GET request to show the registration form
        if ("GET".equals(he.getRequestMethod())) {
        	registrationForm(he);
        } else if ("POST".equals(he.getRequestMethod())) {
            // Handle the registration form submission
            try {
                registerUser(he);
            } catch (SQLException e) {
                sendErrorResponse(he, "Error registering user: " + e.getMessage());
            }
        }
    }

	/**
	 * @param he (object used to send the response).
	 * @throws IOException (if an error occurs while writing a response).
	 */
	private void registrationForm(HttpExchange he) throws IOException {
        String response = 
            "<html>" +
            	//HEad
	            "<head>" +
	            	"<title> Register here </title>" +
	            	"<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\" " +
	            		"integrity=\"sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2\" " +
	            		"crossorigin=\"anonymous\">" +
	            "</head>" +
	            //Body
	            "<body>" +
	            	"<div class=\"container text-center\">" +
	            		"<h1 class=\"display-4 text-dark mb-4\">Register</h1>" +
	            		//Register form
	            		"<form method='POST' action='/register' class=\"d-flex flex-column justify-content-center align-items-center\">" +
			            	"<div class=\"form-group\">" +
			            		"<label for='username'> Username: </label>" +
			            		"<input type='text' id='username' name='username' class=\"form-control\" placeholder=\"Enter username\" required>" +
				            "</div>" +
				            "<div class=\"form-group\">" +
				            	"<label for='password'> Password: </label>" +
				            	"<input type='password' id='password' name='password' class=\"form-control\" placeholder=\"Enter password\" required>" +
				            "</div>" +
				            "<div class=\"form-group\">" +
				            	"<label for='role'>Role:</label>" +
				            	"<select id='role' name='role' class=\"form-control\" required>" +
                                "<option value='admin'> Admin </option>" +
                                "<option value='user'> User </option>" +
                            "</select>" +				            
                            "</div>" +
				            "<button type='submit' class=\"btn btn-primary\"> Register </button>" +
			            "</form>" +
			            "<a href=\"/login\" class=\"btn btn-outline-secondary mt-3\">Back to Login</a>" +
		            "</div>" +
	            "</body>" +
            "</html>";

        he.getResponseHeaders().set("Content-Type", "text/html");
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
	
	/**
	 * @param he (the object used to retrieve data from the form and send a response).
	 * @throws IOException (if an error occurs while reading form data or writing response).
	 * @throws SQLException (if an error occurs while interacting with the users table in the database).
	 */
	private void registerUser(HttpExchange he) throws IOException, SQLException {
        // Parse the POST data from the registration form
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        Map<String, String> params = getParameters(query);

        String username = params.get("username");
        String password = params.get("password");
        String role = params.get("role");

        if (username == null || password == null || role == null) {
            sendErrorResponse(he, "All fields are required.");
            return;
        }
        

        UsersDAO dao = new UsersDAO();
        if (dao.usernameTaken(username)) {
            sendErrorResponse(he, "Username is already taken.");
            return;
        }

        dao.addUser(username, password, role);

        // Redirect to login or a success page
        he.getResponseHeaders().set("Location", "/login");
        he.sendResponseHeaders(302, -1);
    }
	
	
	/**
	 * @param query (query the string to be parsed).
	 * @return (returns a Map containing the name and value params).
	 * @throws UnsupportedEncodingException (if an error occurs while decoding the params values).
	 */
	private Map<String, String> getParameters(String query) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                params.put(parts[0], URLDecoder.decode(parts[1], "UTF-8"));
            }
        }
        return params;
    }
	
	/**
	 * sendErrorResponse display the specific error message.
	 * @param he (the HttpExchange object is used to send a response).
	 * @param message (display error message)
	 * @throws IOException (if an error occurs while writing a response).
	 */
	private void sendErrorResponse(HttpExchange he, String message) throws IOException {
        he.sendResponseHeaders(400, 0);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));
        out.write(message);
        out.close();
    }
}
