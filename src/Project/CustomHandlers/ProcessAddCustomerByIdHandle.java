package Project.CustomHandlers;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.io.IOException;
import java.util.Map;
import com.sun.net.httpserver.HttpHandler;

import Project.Utils;
import Project.Appliances.HomeAppliance;
import Project.Customers.Customer;
import Project.DAOs.CustomerDAO;
import Project.DAOs.HomeApplianceDAO;
import Project.Customers.Address;
 

import com.sun.net.httpserver.HttpExchange;


/**
 * ----- PROCESS & DISPLAY added Customers Handler -----
 * @author Alessandro Frondini - 14501682
 * Features:
 * - The ProcessAddCustomerByIdHandle read the Http post requests with the customer details;
 * - Process the data to add the new customer in the DB;
 * - Returns a HTML response with the customer details to confirm the addition
 * - Use of Bootstrap for styling
 */
public class ProcessAddCustomerByIdHandle implements HttpHandler {
	
	/**
	 * The handle processes the request and display the resulting response with an HTML table
     * @param he (HTTP object containing the  details of the HTTP request/response)
     * @throws IOException (if an IO error occurs during handling)
	 */
	public void handle(HttpExchange he) throws IOException {
		System.out.println("You called --> ProcessAddCustomerByIdHandler");
		
		/*---- Read request -----*/
		try {
			//request body parsing
	        BufferedReader in = new BufferedReader(new InputStreamReader(he.getRequestBody()));
	        StringBuilder requestBody = new StringBuilder();
	        String line;
	        
	        while ((line = in.readLine()) != null) {
	            requestBody.append(line);
	        }
	        
	        //convert params to a Map
	        Map<String, String> params = Utils.requestStringToMap(requestBody.toString());
	        System.out.println("POST Params -> " + params);
	        
	        //Customer details
	        System.out.println(requestBody.toString());	        
	        String businessName = params.get("businessName");
	        String telephoneNumber = params.get("telephoneNumber");        
	        String addressLine0 = params.get("addressLine0");
	        String addressLine1 = params.get("addressLine1");
	        String addressLine2 = params.get("addressLine2");
	        String country = params.get("country");
	        String postCode = params.get("postCode");
	        
	        //Create Address and Customer 
	        Address address = new Address (addressLine0, addressLine1, addressLine2, country, postCode);     
	        Customer customer = new Customer(0, businessName, address, telephoneNumber);

	        //Add customer to DB
	        CustomerDAO customers = new CustomerDAO();
	        customers.addCustomer(customer);
	        
	        //HTML display of response 
	        String htmlResponse = 
	        		"<html>" +
	        		//Head
		            "<head>" +
	    	            "<title> Customer Added </title>" +
	    	            "<meta charset=\"UTF-8\">" +
	    	          //Bootstrap CSS
	    	            "<link rel=\"stylesheet\"   href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\"  "
	    	            + "integrity=\"sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2\"  "
	    	            + "crossorigin=\"anonymous\"  >" +
		            "</head>" +
	    	        //Body    
		            "<body>" +
	    	            "<div class=\"container\">" +
	    	            	"<h1> Customer Added Successfully </h1>" +
	    	            	//Table
	        	            "<table class=\"table table-bordered\">" +
	            	            "<tr><th> Business Name </th><td>" + businessName + "</td></tr>" +
	        	            "</table>" +
	        	            "<table class=\"table table-bordered\">" +
            	            	"<tr><th> Telephone number </th><td>" + telephoneNumber + "</td></tr>" +
            	            "</table>" +
	        	            "<table class=\"table table-bordered\">" +
        	            		"<tr><th> AddressLine 0 </th><td>" + addressLine0 + "</td></tr>" +
        	            	"</table>" +
        	                "<table class=\"table table-bordered\">" +
        	                	"<tr><th> AddressLine 1 </th><td>" + addressLine1 + "</td></tr>" +
     	            		"</table>" +
     	            	    "<table class=\"table table-bordered\">" +
     	            		 	"<tr><th> AddressLine 2 </th><td>" + addressLine2 + "</td></tr>" +
     	            		"</table>" +
     	            		"<table class=\"table table-bordered\">" +
     	            			"<tr><th> Country </th><td>" + country + "</td></tr>" +
    	            		"</table>" +
    	            		"<table class=\"table table-bordered\">" +
 	            				"<tr><th> Postcode </th><td>" + postCode + "</td></tr>" +
 	            			"</table>" +
	            	        //Buttons
	            	        "<div class=\"row mt-4\">" +
		                        "<div class=\"col text-left\">" +
		                            "<a href=\"/home\" class=\"btn btn-primary\"> Back to Home</a>" +
		                        "</div>" +
		                        "<div class=\"col text-right\">" +
		                            "<a href=\"/listAllCustomers\" class=\"btn btn-primary\"> All Customers </a>" +
	                        "</div>" +
	                    "</div>" +
	    	            "</div>" +
		            "</body>" +        
		            "</html>";

	        //Successful response
	        he.sendResponseHeaders(200, htmlResponse.getBytes().length);
	        he.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
	        he.getResponseBody().write(htmlResponse.getBytes());
	             
	        
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


