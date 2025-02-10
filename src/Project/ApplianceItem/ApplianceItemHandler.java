package Project.ApplianceItem;
import Project.Appliances.HomeAppliance;
import Project.DAOs.HomeApplianceDAO;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;

/**
 * ----- ApplianceItem Handler -----
 * @author Alessandro Frondini - 14501682
 * The ApplianceItemHandler fetch product informations from the DB based on the inserted ID and 
 * utilises the {@link HomeApplianceDAO} and displays the result in a HTML response.
 * Usage: 
 * - Sends GET requests in the format (?id=productId) in the header.
 * Dependencies:
 * - {@link HomeApplianceDAO} for database interactions.
 * - {@link HomeAppliance} and {@link ApplianceItem} working together for representing the product info.
 * - Bootstrap CSS for styling the HTML response.
 */
public class ApplianceItemHandler implements HttpHandler {
	
	/**
	 * The handle process the query params of the request URI extracting the item ID.
	 * The data is then retrieved from the DB and the HTML response is generated with the product details
	 * including the details of applianceItem table.
	 * @param  he (is the HttpExchange object for the HTTP request/response.
	 * @throws IOException (if an IO error occurs while writing the response).
	 */
	@Override
    public void handle(HttpExchange he) throws IOException {
		 //send a HTTP status 200 (OK) for the header
		 he.sendResponseHeaders(200, 0);
		 	//prepare a writer for the response
	        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));
	        
	        /**
	         * Parse the query for the ID (-1: default/sentinel value for invalid or uninitialised state)
	         */
	        String query = he.getRequestURI().getQuery();
	        int productId = -1;
	        
	        if (query != null && query.contains("id=")) {
	            for (String param : query.split("&")) {
	                if (param.startsWith("id=")) {
	                    productId = Integer.parseInt(param.split("=")[1]);
	                }
	            }
	        }
	        System.out.println("Received productId: " + productId);

	        //Case handler when no ID is provided
	        if (productId == -1) {
	            out.write("<html><body><h1> Product not found </h1></body></html>");
	            out.close();
	            return;
	        }
	        
	        //GET products details from DB
	        HomeApplianceDAO applianceDAO = new HomeApplianceDAO();
	        HomeAppliance appliance = null;
	        
	        try {
	            appliance = applianceDAO.getProductById(productId);
	            System.out.println("Fetched appliance: " + appliance);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }

	        //Case handler when no product is available
	        if (appliance.getApplianceItem() == null) {
	        	
	            out.write("<html><body><h1> Sorry product unavailable :( </h1></body></html>");
	            out.close();
	            return;
	        }

	        //Retrieved applianceItem
	        ApplianceItem applianceItem = appliance.getApplianceItem();
	        
	        //HTML response
	        out.write(
	                "<html>" +
	                    "<head>" +
	                        "<title>Product Details</title>" +
	                        "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\">" +
	                        "<style>body { margin: 20px; }</style>" +
	                    "</head>" +
	                    "<body>" +
	                        "<div class=\"container\">" +
	                            "<h1>Product Details</h1>" +
	                            "<table class=\"table table-bordered\">" +
	                                "<tr><th>ID</th><td>" + appliance.getId() + "</td></tr>" +
	                                "<tr><th>SKU</th><td>" + appliance.getSku() + "</td></tr>" +
	                                "<tr><th>Description</th><td>" + appliance.getDescription() + "</td></tr>" +
	                                "<tr><th>Category</th><td>" + appliance.getCategory() + "</td></tr>" +
	                                "<tr><th>Price</th><td>" + appliance.getPrice() + "</td></tr>" +
	                                (applianceItem != null ? 
	                                "<tr><th>Warranty Years</th><td>" + applianceItem.getWarrantyYears() + "</td></tr>" +
	                                "<tr><th>Brand</th><td>" + applianceItem.getBrand() + "</td></tr>" +
	                                "<tr><th>Model</th><td>" + applianceItem.getModel() + "</td></tr>"
	                                : "<tr><th colspan=\"2\">No additional details available</th></tr>") +
	                            "</table>" +
	                            "<a href=\"/listAllProducts\" class=\"btn btn-primary\">Back to Products</a>" +
	                        "</div>" +
	                    "</body>" +
	                "</html>"
	        );

	        out.close();
	}
}
