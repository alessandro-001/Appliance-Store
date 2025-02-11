package Project.CustomHandlers;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.sun.net.httpserver.HttpHandler;

import Project.Appliances.HomeAppliance;
import Project.Customers.Customer;
import Project.DAOs.CustomerDAO;

import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.sql.SQLException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * ----- Search by Customer ID Handler -----
 * @author Alessandro Frondini
 * The SearchByCustomerIdHandle handles the HTTP requests for searching a customer by its ID
 * Features:
 * - Process HTTP GET requests
 * - Retrieve the Customer ID from the DB
 * - Generated a HTML page with the resulting customer details
 * - Bootstrap CSS integration for styling
 */
public class SearchByCustomerIdHandle implements HttpHandler {
	
	/**
	 * The handle manage the HTTP requests to get the customer ID, query the DB and display the result in a HTML page.
     * @param he (HTTP object containing the  details of the HTTP request/response)
     * @throws IOException (if an IO error occurs during handling)
	 */
	public void handle(HttpExchange he) throws IOException {
		he.sendResponseHeaders(200,0); //OK
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));
		
		//GET query string from URI
		String query = he.getRequestURI().getQuery();
		String customerId = null;
		
		//Extract the ID params
		if (query != null) {
			String[] queryParameters = query.split("&");
			 
			 for (String param : queryParameters) {
				 String[] keyValue = param.split("=");
				 
	                if (keyValue[0].equals("id")) {
	                	customerId = keyValue[1];
	                }
			 }
		 }
		 
		 StringBuilder tableRows = new StringBuilder();
		 
		 /*----- SEARCH CUSTOMER by ID method -----*/
		 if (customerId != null) {
			 CustomerDAO customerDAO = new CustomerDAO();
			 
			 try {
				 ArrayList<Customer> customers = customerDAO.searchCustomerById(Integer.parseInt(customerId));
				 
				 if (customers.isEmpty()) {
	    			  tableRows.append("<tr><td colspan='5'> :( No custoemr found for this ID :( </td></tr>");
	    		 
	    		  } else {
	    			  
	    			  for (Customer customer : customers) {
	    				  tableRows.append(
	    						  "<tr>" +
	    								  "<td>" + customer.getCustomerId() + "</td>" +
	    								  "<td>" + customer.getBusinessName() + "</td>" +
	    								  "<td>" + customer.getAddress() + "</td>" +
	    								  "<td>" + customer.getTelephoneNumber() + "</td>" +
	    						  "</tr>");
	    			  }
	    		  }
				 
			 } catch (Exception e) {
	    		  e.printStackTrace();
	    		  tableRows.append("<tr><td colspan='5'> Error in extracting data </td> </tr>");
	    	 }
		 }
		 
		 
		 /*---- SEARCH APPLIANCE by ID HTML ----*/
		  out.write(
			  "<html>" +
				//Head
	            "<head>" +
	            	"<title> Search Customer </title>" +
	            	//Bootstrap CSS
	            	"<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\" integrity=\"sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2\" crossorigin=\"anonymous\">" +
	            "</head>" +
	            //Body
	            "<body style = \"background-color: white; text-align: center;\" >" +
		            "<div class = \"container mt-5\">" +
		                "<h1 class = \"text-center\" style=\"font-size: 36px; color: black;\" > Search Customers </h1>" +
		                //Search form
		                "<form action = \"/searchById\" method =\"get\" class = \"form-inline justify-content-center\">" +
		                    "<div class=\"form-group\">" +
		                        "<input type= \"text\" name= \"id\" class= \"form-control mr-2\" placeholder= \"Enter Customer ID\" style= \"font-size: 15px;\">" +
		                    "</div>" +
		                    "<button type= \"submit\" class= \"btn btn-primary\"> Search </button>" +
		                "</form>" +
	                	//Table
						"<table class= \"table table-bordered table-striped mt-5\">" +
							"<thead class= \"thead-dark\">" +
							    "<tr>" +
							    "<th> ID </th>" +
		                        "<th> Business Name </th>" +
		                        "<th> Address </th>" +
		                        "<th> Phone Number </th>" +
							    "</tr>" +
							"</thead>" +
							"<tbody>" +
							    tableRows.toString() +
							"</tbody>" +
						"</table>" +
						//Back button
						"<form action=\"/listAllCustomers\" class=\"mt-4\">" +
							"<button type=\"submit\" class=\"btn btn-primary\"> Go Back </button>" +
						"</form>" +
					"</div>" +
				"</body>" +
					
				"</html>"
		    );
		 
		  out.close();
	}
}
