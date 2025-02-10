package Project.Handlers;

import Project.Utils;
import Project.AdminArea.SessionHandler;
import Project.AdminArea.Users;
import java.io.*;
import Project.AdminArea.UsersDAO;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

/**
 * @author Alessandro Frondini - 14501682
 * The LoginHandler manages HttpHandler requests for the log in functions, processes the 
 * user login, validates the credentials and redirects the user based on it role.
 */
public class LoginHandler implements HttpHandler {
	
	/**
	 * handle the incoming http requests
     * @param he (HTTP object containing the  details of the HTTP request/response)
     * @throws IOException (if an IO error occurs during handling)	 
	 */
    @Override
    public void handle(HttpExchange he) throws IOException {

    	if ("POST".equals(he.getRequestMethod())) {
            try {
                processLogin(he);
            } catch (SQLException e) {
                sendErrorResponse(he, "Database error: " + e.getMessage());
            }
        } else {
            displayLoginForm(he); 
        }
    }
   
    private void displayLoginForm(HttpExchange he) throws IOException {
        String response =
            "<html>" +
            	//Head
                "<head>" +
                    "<title> Login </title>" +
                    "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\" " +
                    "integrity=\"sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2\" " +
                    "crossorigin=\"anonymous\">" +
                "</head>" +
                //Body
                "<body style=\"background-color: white;\">" +
                    "<div class=\"container text-center pt-5\">" +
                        "<h1 class=\"display-4 text-dark mb-4\">Login</h1>" +
                        //login form
                        "<form method='POST' action='/login' class=\"d-flex flex-column justify-content-center align-items-center\">" +
                            "<div class=\"form-group mb-3 w-50\">" +
                                "<label for='username' class=\"h5\"> Username: </label>" +
                                "<input type='text' id='username' name='username' class=\"form-control form-control-lg\" placeholder=\"Enter username\" >" +
                            "</div>" +
                            "<div class=\"form-group mb-3 w-50\">" +
                                "<label for='password' class=\"h5\"> Password:</label>" +
                                "<input type='password' id='password' name='password' class=\"form-control form-control-lg\" placeholder=\"Enter password\" >" +
                            "</div>" +
                            //remember me option
                            "<div class=\"checkbox mb-3\">" +
                            	"<label> <input type=\"checkbox\" value=\"remember-me\"> Remember me </label>" +
                            "</div>" +
                            //Login button
                            "<button type='submit' class=\"btn btn-lg btn-primary w-50 mt-4\"> Login </button>" +
                        "</form>" +
                        //Register button
                       "<a href=\"/register\" class=\"btn btn-lg btn-outline-primary w-50 mt-4 \"> Register </a>" +

                    "</div>" +
                "</body>" +
            "</html>";

        //Set HTTP
        he.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");

        //Response body
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    
    
    
	  /**
	   * --- Process Login ---
	   * This method process the login form locally and redirects to the appropriate page based on the role
	   * @param he (HTTP object containing the  details of the HTTP request/response)
       * @throws IOException (if an IO error occurs during handling)
       * @throws SQLException (if a database error occurs while retrieving the user data)	 
	   */
	  private void processLogin(HttpExchange he) throws IOException, SQLException {
	      System.out.println("You called ---> processLogin");
	  
	      //Parse the data
	      InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "UTF-8");
	      BufferedReader br = new BufferedReader(isr);
	      String query = br.readLine();
	      System.out.println("POST data: " + query);
	  
	      //Parse the parameters
	      Map<String, String> params = getParameters(query);
	      String username = params.get("username");
	      String password = params.get("password");
	      password = password.trim(); //cut eventual excess
	  
	  
	      if (username == null || password == null) {
	          sendErrorResponse(he, "Username or password missing");
	          return;
	      }
	  
	      //Validate user
	      UsersDAO dao = new UsersDAO();
	      Users userName = dao.getUserByUsername(username);
	      
	      if (userName != null) {
	          String dbPassword = userName.getUsersPassword();
	          
	          //Debug to remove
	          System.out.println("User found: " + userName.getUsersName() + " - "+ dbPassword); 
	          System.out.println("Plain-text Password: " + password);
	          System.out.println("Hashed Password from DB: " + dbPassword);
	          System.out.println("Password Match Result: " + BCrypt.checkpw(password, dbPassword));
	      }
	      
	      if (userName == null) {
	          sendErrorResponse(he, "Invalid username or password.");
	          return;
	      }
	          
	      try {        
	      	if (BCrypt.checkpw(password, userName.getUsersPassword())) {
	      		System.out.println("password match??? ---> " + BCrypt.checkpw(password, userName.getUsersPassword()));
	              // Successful login if password matches
	              String userRole = userName.getUsersRole();
	              
	              String sessionId = URLEncoder.encode(userRole, "UTF-8"); // Encode session ID for safety
	              Utils.setTheCookies(he, "role", sessionId);
	              
	              //Redirect to admin dashboard
	              if ("admin".equals(userRole)) {
	                  System.out.println("// entered super admin area //");
	                  redirectToPage(he, "/home");
	               
	              //if normal user redirect to list of all products    
	              } else if ("user".equals(userRole)) {
	                  System.out.println("// entered normal user area //");
	                  redirectToPage(he, "/listAllProducts");
	              }
	          } else {
	              sendErrorResponse(he, "Username or password not valid :(");
	          }
	      	
	      } catch (Exception e) {
	          e.printStackTrace();
	          sendErrorResponse(he, "An internal error occurred.");
	      }
	      
	  }



    /**
     * --- Page redirector ---
     * @param he {@link HttpExchange} object used to send response
     * @param path (relative path to redirect the client)
     * @throws IOException (if an IO error occurs during handling)
     */
    private void redirectToPage(HttpExchange he, String path) throws IOException {
        he.getResponseHeaders().set("Location", path);
        he.sendResponseHeaders(302, -1);
    }

    /**
     * --- Error response ---
     * @param he {@link HttpExchange} object used to send response
     * @param message (error message)
     * @throws IOException (if an IO error occurs during handling)
     */
    private void sendErrorResponse(HttpExchange he, String message) throws IOException {
        he.sendResponseHeaders(400, 0);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));
        out.write(message);
        out.close();
    }

    /**
     * --- GET user parameters --- 
     * @param query (the String to parse)
     * @return a map with the parameters
     * @throws UnsupportedEncodingException (for unsupported encoding)
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
}


