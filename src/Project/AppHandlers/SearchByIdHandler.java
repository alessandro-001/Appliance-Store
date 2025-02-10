package Project.AppHandlers;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.sun.net.httpserver.HttpHandler;

import Project.Appliances.HomeAppliance;
import Project.DAOs.HomeApplianceDAO;

import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.sql.SQLException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * ----- Search by ID Handler -----
 * @author Alessandro Frondini - 14501682
 * This handler allows admins to search for an appliance by its ID.
 * It receive the ID from the query, interacts with the DB and renders an HTML page to display the product.
 * The handler also provides a search form for the user to enter the product ID and submit the request to the server.
 * Dependencies:
 * - {@link HomeApplianceDAO}: DAO used to interact with the database and retrieve appliance details;
 * - {@link Project.Appliances.HomeAppliance}: Model class for appliance object with its properties.
 * 
 * - Bootstrap CSS for styling the HTML response.
 */
public class SearchByIdHandler implements HttpHandler {
	  
	public void handle(HttpExchange he) throws IOException {
		  he.sendResponseHeaders(200,0);
		  BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));
		  
		  String query = he.getRequestURI().getQuery();
	      String productId = null;
	      String productDescription = null;
	      
	      if (query != null) {
	            String[] queryParameters = query.split("&");
	            
	            for (String param : queryParameters) {
	                String[] keyValue = param.split("=");
	                
	                if (keyValue[0].equals("id")) {
	                    productId = keyValue[1];
	                    
	                } else if (keyValue[0].equals("description")) {
	                    productDescription = keyValue[1];
	                }
	            }
	       }
	      
	      StringBuilder tableRows = new StringBuilder();
	      
	      /*----- SEARCH APPLIANCE by ID method -----*/
	      if (productId != null) {
	    	  HomeApplianceDAO applianceDAO = new HomeApplianceDAO();
	    	  
	    	  try {
	    		  ArrayList<HomeAppliance> appliances = applianceDAO.searchProductById(Integer.parseInt(productId));
	    		  
	    		  if (appliances.isEmpty()) {
	    			  tableRows.append("<tr><td colspan='5'> :( No products found for this ID :( </td></tr>");
	    		 
	    		  } else {
	    			  for (HomeAppliance appliance : appliances) {
	    				  tableRows.append(
	    						  "<tr>" +
	    								  "<td>" + appliance.getId() + "</td>" +
	    								  "<td>" + appliance.getSku() + "</td>" +
	    								  "<td>" + appliance.getDescription() + "</td>" +
	    								  "<td>" + appliance.getCategory() + "</td>" +
	    								  "<td>" + appliance.getPrice() + "</td>" +
	    						  "</tr>");
	    			  }
	    		  }
	    	  } catch (Exception e) {
	    		  e.printStackTrace();
	    		  tableRows.append("<tr><td colspan='5'> Error in extracting data </td> </tr>");
	    	  }
	    	  
	      }
	     
		  	      
		  
		  /**
		   * ---- SEARCH APPLIANCE by ID HTML ----
		   * The rendered HTML content includes the following:
	       * - A search form to enter a product ID and submit the search request;
	       * - A table displaying the product details (ID, SKU, description, category, price);
	       * - Error messages for invalid input or database errors;
	       * - Buttons to go back to the appliances list.
		   */
		  out.write(
			  "<html>" +
				// Head
	            "<head>" +
	            	"<title>Search Product</title>" +
	            	//Bootstrap CSS
	            	"<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\" integrity=\"sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2\" crossorigin=\"anonymous\">" +
	            "</head>" +
	            // Body
	            "<body style = \"background-color: white; text-align: center;\" >" +
		            "<div class = \"container mt-5\">" +
		                "<h1 class = \"text-center\" style=\"font-size: 36px; color: black;\" > Search Products </h1>" +
		                // Search form
		                "<form action = \"/searchById\" method =\"get\" class = \"form-inline justify-content-center\">" +
		                    "<div class=\"form-group\">" +
		                        "<input type= \"text\" name= \"id\" class= \"form-control mr-2\" placeholder= \"Enter Product ID\" style= \"font-size: 15px;\">" +
		                    "</div>" +
		                    "<button type= \"submit\" class= \"btn btn-primary\"> Search </button>" +
		                "</form>" +
	                	// Table
						"<table class= \"table table-bordered table-striped mt-5\">" +
							"<thead class= \"thead-dark\">" +
							    "<tr>" +
							        "<th> ID </th>" +
							        "<th> SKU </th>" +
							        "<th> Description </th>" +
							        "<th> Category </th>" +
							        "<th> Price £ </th>" +
							    "</tr>" +
							"</thead>" +
							"<tbody>" +
							    tableRows.toString() +
							"</tbody>" +
						"</table>" +
						// Back button
						"<form action=\"/listAllProducts\" class=\"mt-4\">" +
							"<button type=\"submit\" class=\"btn btn-primary\"> Go Back </button>" +
						"</form>" +
					"</div>" +
				"</body>" +
					
				"</html>"
		    );
		 
		  out.close();
		}
}



/* StuId: 14501682 */
