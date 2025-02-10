package Project.CustomHandlers;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;
import java.util.function.IntSupplier;

/**
 * @author Alessandro Frondini
 * The AddByCustomerIdHandle generates the HTML response to display a form to ad new customers in the
 * Database. After the form is submitted, the administrator is redirected to the {@link processAddCustomerById} 
 * endpoint for further processing of data.
 * Features:
 * - display HTML form to add customer's details.
 * - Bootstrap CSS for styling.
 * - Redirects form submissions to an appropriate endpoint.
 */
public class AddByCustomerIdHandle implements HttpHandler {
	
	/**
	 * The handle method responds with a HTML form to add customers details, redirecting to
	 * a process page endpoint.
     * @param he (HTTP object containing the  details of the HTTP request/response)
     * @throws IOException (if an IO error occurs during handling)	 
     */
	public void handle(HttpExchange he) throws IOException {
		System.out.println("You called ---> AddByCustomerIdHandler");
		 
		he.sendResponseHeaders(200, 0);
	    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));
	        
	    /*---- ADD CUSTOMER by ID HTML----*/
	    out.write(
	    	    "<html>" +
	    	        // Head
	    	        "<head>" +
	    	            "<title> Add a Customer </title>" +
	    	            // Bootstrap
	    	            "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\">" +
	    	        "</head>" +
	    	        // Body
	    	        "<h1 style=\"color: black; font-size: 36px; margin-bottom: 20px; text-align: center\"> Add a Customer </h1>" +
	    	        "<body style=\"background-color: white; text-align: left; padding: 20px;\">" +
	    	            // Form fields
	    	            "<div class=\"container\" style=\"max-width: 600px;\">" +
	    	            "<form action=\"/processAddCustomerById\" method=\"post\">" +
	    	                // Add Business Name
	    	                "<div class=\"form-group\">" +
	    	                    "<label for=\"businessName\" class=\"mr-2\"> Business name: </label>" +
	    	                    "<input type=\"text\" name=\"businessName\" id=\"businessName\" class=\"form-control mr-2\" placeholder=\"Enter Business Name\" required>" +      
	    	                "</div>" +
	    	                //Add phone number
	    	                "<div class=\"form-group\">" +
    	                    	"<label for=\"businessName\" class=\"mr-2\"> Telephone number: </label>" +
    	                    	"<input type=\"text\" name=\"telephoneNumber\" id=\"telephoneNumber\" class=\"form-control mr-2\" placeholder=\"Enter Telephone Number\" required>" +      
    	                    "</div>" +
    	                    //Add address 0
    	                    "<div class=\"form-group\">" +
	                    		"<label for=\"addressLine0\" class=\"mr-2\"> AddressLine 0: </label>" +
	                    		"<input type=\"text\" name=\"addressLine0\" id=\"addressLine0\" class=\"form-control mr-2\" placeholder=\"Enter address line 0\" required>" +      
	                    	"</div>" +
	                    	//Add address 1
	                    	"<div class=\"form-group\">" +
                    			"<label for=\"addressLine1\" class=\"mr-2\"> AddressLine 1: </label>" +
                    			"<input type=\"text\" name=\"addressLine1\" id=\"addressLine1\" class=\"form-control mr-2\" placeholder=\"Enter address line 1\" required>" +      
                    		"</div>" +	
                    		//Add address 2
                    		"<div class=\"form-group\">" +
                    			"<label for=\"addressLine2\" class=\"mr-2\"> AddressLine 2: </label>" +
                    			"<input type=\"text\" name=\"addressLine2\" id=\"addressLine2\" class=\"form-control mr-2\" placeholder=\"Enter address line 2\" required>" +      
                    		"</div>" +
                    		//Add country
                    		"<div class=\"form-group\">" +
                    			"<label for=\"addressLine0\" class=\"mr-2\"> Country: </label>" +
                    			"<input type=\"text\" name=\"country\" id=\"country\" class=\"form-control mr-2\" placeholder=\"Enter Country\" required>" +      
                    		"</div>" +
                    		//Add Post code
                    		"<div class=\"form-group\">" +
                				"<label for=\"postCode\" class=\"mr-2\"> Postcode: </label>" +
                				"<input type=\"text\" name=\"postCode\" id=\"postCode\" class=\"form-control mr-2\" placeholder=\"Enter Postcode\" required>" +      
                			"</div>" +  	
	    	                // Buttons
	    	                "<div class=\"d-flex justify-content-between mt-4\">" +
	    	                    "<button type=\"submit\" class=\"btn btn-success\"> Add Customer </button>" +
	    	                    "<a href=\"/home\" class=\"btn btn-primary\"> Back to Home </a>" +
	    	                "</div>" +
	    	            "</form>" +
	    	        "</div>" +
	    	        "</body>" +
	    	    "</html>");
	    
	    	out.close();

	}

}
