package Project.CustomHandlers;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.io.IOException;
import java.util.Map;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import Project.Utils;
import Project.Customers.Customer;
import Project.DAOs.CustomerDAO;


/**
 * ----- Process & Display added Customers Handler -----
 * @author Alessandro Frondini
 * The ProcessDeleteCustomerByIdHandle process the http requests to delete a customer from the DB
 * Features:
 * - Process HTTP requests
 * - Extract customer ID from request body
 * - Delete corresponding customer from the DB
 * - Generates a HMTL page with the confirmation of successful deletion or return errors
 */
public class ProcessDeleteCustomerByIdHandle implements HttpHandler {
	
	 /**
	  * The handle interacts with the {@link CustomerDAO} for the deletion of the customer from the DB
      * @param he (HTTP object containing the  details of the HTTP request/response)
      * @throws IOException (if an IO error occurs during handling)
	  */
	 public void handle(HttpExchange he) throws IOException {
		 System.out.println("You called --> ProcessDeleteCustomerByIdHandler");
		 
		 //Read request
		 try {
			 BufferedReader in = new BufferedReader(new InputStreamReader(he.getRequestBody()));
		     StringBuilder requestBody = new StringBuilder();
		     String line;
		     
		     while ((line = in.readLine()) != null) {
		            requestBody.append(line);
		     }
		     
		     Map<String, String> params = Utils.requestStringToMap(requestBody.toString());
		     System.out.println("POST Params -> " + params);
		     
		     String customerId = params.get("customerID");
		     
		     
		     if (customerId == null || customerId.isEmpty()) {
		         he.sendResponseHeaders(400, -1); //Bad Request
		         return;
		     }

		     int customId = Integer.parseInt(customerId); //Parse ID to integer
	            
		     //Delete from DB
		     CustomerDAO customerDAO = new CustomerDAO();
		     Customer customer = customerDAO.getCustomerId(customId);
	            
		     if (customer == null) {
		         he.sendResponseHeaders(404, -1); //Not Found
		         return;
		     }
	            
		     customerDAO.deleteCustomerById(customId);
	            
		     //HTML response
		     String htmlResponse = "<html>"
				            		+ "<body>"
				            			+ "<h1> Customer Deleted Successfully </h1>"
				            			+ "<a href=\"/listAllProducts\"> Back to List </a>"
				            		+ "</body>"
				            		+ "</html>";

		     he.sendResponseHeaders(200, htmlResponse.getBytes().length);
		     he.getResponseBody().write(htmlResponse.getBytes());
			 
		     
			/*---- Error catching blocks ----*/
		    } catch (SQLException e) {
		        System.err.println("SQL ERROR: " + e.getMessage());
		        he.sendResponseHeaders(500, -1); // Internal Server Error

		    } catch (NumberFormatException e) {
		        System.err.println("Invalid Input: " + e.getMessage());
		        he.sendResponseHeaders(400, -1); // Bad Request

		    } catch (Exception e) {
		        System.err.println("Unexpected ERROR: " + e.getMessage());
		        he.sendResponseHeaders(500, -1); // Internal Server Error

		    } finally {
		        he.getResponseBody().close();
		    }
	 }
}
