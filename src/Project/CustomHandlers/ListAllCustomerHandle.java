package Project.CustomHandlers;

import Project.Appliances.HomeAppliance;
import Project.Customers.Customer;
import Project.DAOs.CustomerDAO;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.sql.SQLException;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * @author Alessandro Frondini
 * The ListAllCustomerHandle retrieves all the customer records from the DB, displays an HTML
 * table with the details, and provides additional actions (like update or delete).
 * The logic implemented in the /login handle, allows only admins to access this page exclusively
 * to be able to support the customers.
 * Features:
 * - Retrieves all customers using {@link CustomerDAO}
 * - Use of Bootstrap CSS to display the customers table
 * - Enable additional actions based on the role admin.
 */
public class ListAllCustomerHandle implements HttpHandler {

	/**
	 * This handle retrieves the user sole from the http request, fetch customers data from the DB
	 * and displays it in a html page.
	 * @param he (HTTP object containing the  details of the HTTP request/response)
     * @throws IOException (if an IO error occurs during handling)
	 */
	public void handle(HttpExchange he) throws IOException {
		he.sendResponseHeaders(200,0);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));		
		System.out.println("Received request ---> " + he.getRequestURI());
		
		//Get the user role
		String userRole = getSessionUserRole(he); 
		CustomerDAO customers = new CustomerDAO();
		
		
			
		/**---- Customer Table -----*/
		StringBuilder tableRows = new StringBuilder();
		try {
			ArrayList<Customer> allCustomers = customers.listAllCustomers();
			System.out.println("User role entered ---> " + userRole);
			
			//Table rows
		    for (Customer customer : allCustomers) {
		    	tableRows.append(
	    			"<tr>" +
	                  "<td class=\"align-middle text-center\">" + customer.getCustomerId() + "</td>" +
	                  "<td class=\"align-middle text-center\">" + customer.getBusinessName() + "</td>" +
	                  "<td class=\"align-middle text-center\">" + customer.getAddress() + "</td>" +
	                  "<td class=\"align-middle text-center\">" + customer.getTelephoneNumber() + "</td>" );
	                  
	                      
	    		      //Show Actions column only if the user = admin
					  if (userRole.equals("admin")) {
		                  tableRows.append(
	                		  "<td class=\"align-middle text-center\">" + 
                				  "<div class=\"d-flex justify-content-center\">" +
                				  	"<form action=\"/deleteCustomerById\" method=\"get\" class=\"mr-2\">" +
                				  		"<input type=\"hidden\" name=\"id\" value=\"" + customer.getCustomerId() + "\" />" +
                				  		"<button type=\"submit\" class=\"btn btn-outline-danger btn-sm\"> Delete </button>" +
                				  	"</form>" +
                				  	"<form action=\"/updateCustomerById\" method=\"get\">" +
                				  		"<input type=\"hidden\" name=\"id\" value=\"" + customer.getCustomerId() + "\" />" +
                				  		"<button type=\"submit\" class=\"btn btn-outline-warning btn-sm\"> Update </button>" +
                			 		"</form>" +
                				  "</div>" +
                			  "</td>" );
					  } else if (userRole.equals("user")) {
						  
						  //Display nothing if role accidentally = user
		            	  tableRows.append("<td class=\"align-middle text-center\"> - </td>");
		              }                     
		    }
			
		} catch (Exception e) {
		      e.printStackTrace();
		}
		
		
		/**---- LIST of ALL CUSTOMERS HTML ----*/
		  out.write(
		    "<html>" +
		    	//Head
				"<head> "
				+ "<title> List Of All Customers </title> "+
					//Bootstrap CSS
				 	"<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\" " +
				    "integrity=\"sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2\" " +
				   "crossorigin=\"anonymous\">" +
			    "</head>" +
				//Body	
			    "<body style=\"background-color: white; text-align: center \" >" +
			    "<div class=\"container\">" +
				    "<div class=\"d-flex justify-content-between align-items-center mt-3\">" +
	                "<h1 style=\"color: black; font-size: 40px;\"> List of All Customers </h1>" +
		                "<form action=\"/searchByCustomerId\" method=\"get\" class=\"form-inline\">" +
		                    "<input type=\"text\" name=\"id\" class=\"form-control mr-2\" placeholder=\"Enter Customer ID\" required>" +
		                    "<button type=\"submit\" class=\"btn btn-dark\"> Search </button>" +
		                "</form>" +
	                "</div>" +			    	
	                //Table
			    	"<table class=\"table table-bordered table-striped table-dark mt-4\"  >" +
		                "<thead class=\"thead-dark\"  >" +
		                    "<tr>" +
		                        "<th> ID </th>" +
		                        "<th> Business Name </th>" +
		                        "<th> Address </th>" +
		                        "<th> Phone Number </th>" +
		                        "<th> Actions </th>" +
		                    "</tr>" +
		                "</thead>" +
		                "<tbody>" +
		                    tableRows.toString() +
		                "</tbody>" +
	                "</table>" +
		            //Back Button  
	                "<form action=\"/\" class=\"mt-4\">" +
		                "<div class=\"row mt-4\">" +
	                    "<div class=\"col text-left\">" +
	                        "<a href=\"/home\" class=\"btn btn-primary\"> Back to Home </a>" +
	                    "</div>");
				  			
		  if (userRole.equals("admin")) {
              out.write(
              "<div class=\"col text-right\">" + 
            		  "<a href=\"/addCustomerById\" class=\"btn btn-success\"> Add a Customer </a>" +
              "</div>" );
		  }          
                    
		  out.write(
			      "</form>" +
			      "</body>" +
			      "</html>");
		  
	      out.close();
	}
	
	
	/**
	 * Extract the user role via the query params, cookies or referer header
	 * @param he (HttpExchange object representing the request)
	 * @return the user role as String
	 */
	private String getSessionUserRole(HttpExchange he) {
	    //GET role from query
	    String query = he.getRequestURI().getQuery();
	    if (query != null && query.contains("role=")) {
	        for (String param : query.split("&")) {
	            if (param.startsWith("role=")) {
	                System.out.println("Role from query: " + param.split("=")[1]);
	                return param.split("=")[1];
	            }
	        }
	    }

	    //GET role from cookies
	    if (he.getRequestHeaders().containsKey("Cookie")) {
	        String cookies = he.getRequestHeaders().getFirst("Cookie");
	        System.out.println("Cookies: " + cookies);
	        for (String cookie : cookies.split("; ")) {
	            if (cookie.startsWith("role=")) {
	                return cookie.split("=")[1];
	            }
	        }
	    }

	    //GET role from Referer 
	    String ref = he.getRequestHeaders().getFirst("Referer");
	    
	    if (ref != null && ref.contains("role=")) {
	        for (String param : ref.split("\\?")[1].split("&")) {
	            if (param.startsWith("role=")) {
	                System.out.println("Role from referer: " + param.split("=")[1]);
	                return param.split("=")[1];
	            }
	        }
	    }

	    //Back to default
	    return "user";
	}

	
}




/* StuId: 14501682 */
