package Project.Handlers;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.sun.net.httpserver.HttpHandler;

import Project.AdminArea.UsersDAO;

import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.sql.SQLException;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * ----- Root Handler -----
 * @author Alessandro Frondini - 14501682
 * the RootHandler manages http requests for the root paths, generates the homepage HTML used mainly for admins,
 * and manage the user roles and cookies for session handling
 */
public class RootHandler implements HttpHandler{
	
	/**
	 * Handle the incoming http request
     * @param he (HTTP object containing the  details of the HTTP request/response)
     * @throws IOException (if an IO error occurs during handling)	 
	 */
	public void handle(HttpExchange he) throws IOException {
	  he.sendResponseHeaders(200,0);
	  BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));
	  	  
	  //retrieve the parameters
	  Map<String, String> queryParams = parseQueryParams(he.getRequestURI().getQuery());
      String usernameParams = queryParams.get("username");
      String roleParams = queryParams.get("role");
      

	    //GET role from cookies
	    if (he.getRequestHeaders().containsKey("Cookie")) {
	        String cookies = he.getRequestHeaders().getFirst("Cookie");
	        System.out.println("Cookies: " + cookies);
	        for (String cookie : cookies.split("; ")) {
	            if (cookie.startsWith("role=")) {
	                roleParams = cookie.split("=")[1];
	            }
	        }
	    }

      
      //default roles
      String username = "User"; 
      String role = "Guest";   
      
      if (usernameParams != null) {
    	  UsersDAO usersDAO = new UsersDAO();
    	  try {
              username = usersDAO.getUsernameById(usernameParams);
              role = usersDAO.getUserRoleById(usernameParams);
              setTheCookies(he, "username", username);
          } catch (Exception ex) {
              ex.printStackTrace();
          }
      }

	  
	  /*---- HOMEPAGE HTML ----*/ 
	  out.write(
			    "<html>" +
			    // Head
			    "<head> " +
				    "<title> The Home Appliance </title> "+
			    	// Bootstrap CSS
				    "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\" " +
				    "integrity=\"sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2\" " +
				    "crossorigin=\"anonymous\" >" +
			    "</head>" +
			    //Body
			    "<body style=\"background-color: white;\">" +
			    	//Header 
			    	"<div class=\"container text-center mb-5 pt-5\">" +
			    		"<h1 class=\"display-4 text-dark\" > Home Appliances </h1><br><br>" + 
			    		"<span class=\"badge badge-pill badge-dark \">" + roleParams + "</span>"  +
			    	"</div>" +
			    	//Menu
			    	"<div class=\"container\">" +
			    		"<div class=\"row  justify-content-center\">" +
			    			"<div class=\"col-sm-12 col-md-8 col-lg-6\">" +
			    				"<ul class=\"list-group \">" +
			    					"<li class=\"list-group-item text-center\"><a href=\"/listAllProducts\"  class=\"btn btn-lg btn-primary w-100 \"> Display All Appliances </a></li>" +
			    					"<li class=\"list-group-item text-center\"><a href=\"/listAllCustomers\" class=\"btn btn-lg  btn-primary w-100 \" > Display All Customers </a></li>" +
			    				"</ul>" +
			    				"<div class=\"text-center mt-3\">" +
			    					"<a href=\"/login\" class=\"btn btn-link\"> Back to Login </a>" +
		                        "</div>" +
			    			"</div>" +
			    		"</div>" +
			    	"</div>" +
			    "</body>" +
			    "</html>"
			    );
	 
	  out.close();

	}
	
	
	//parse the query string
	private Map<String, String> parseQueryParams(String query) {
		if (query == null || query.isEmpty()) {
	        return new HashMap<>(); 
	    }
        
        return java.util.Arrays.stream(query.split("&"))
            .map(param -> param.split("="))
            .filter(keyValue -> keyValue.length == 2)
            .collect(Collectors.toMap(keyValue -> keyValue[0], keyValue -> keyValue[1]));
    }
	
	
	//Get the cookies
	private Map<String, String> getTheCookies(List<String> cookiez) {
		 
        return cookiez == null ? new HashMap<>() :
        	cookiez.stream()
                .map(cookie -> cookie.split("="))
                .filter(cookieParts -> cookieParts.length == 2)
                .collect(Collectors.toMap(cookieParts -> cookieParts[0], cookieParts -> cookieParts[1]));
    }
	
	//Set cookies response
	private void setTheCookies(HttpExchange he, String name, String value) {
        String cookie = name + "=" + value + "; Path=/; HttpOnly";
        he.getResponseHeaders().add("Set-Cookie", cookie);
    }

}
